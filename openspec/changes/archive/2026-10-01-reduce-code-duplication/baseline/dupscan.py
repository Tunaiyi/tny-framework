#!/usr/bin/env python3
"""CPD 风格 Java 克隆扫描器（规划期量化用）。
模式:
  M1 exact   token 原样（标识符+字面量都算），窗口 80 token
  M2 litnorm 字符串/字符/数字字面量归一 LIT，窗口 80
  M3 struct  M2 基础上非关键字标识符全部归一 ID（纯结构骨架），窗口 140
输出 /tmp/dupreport.md（人读 Top 榜+模块聚合）与 /tmp/dupreport.json（明细）。
"""
import os, re, sys, json, bisect
from collections import defaultdict

ROOT = "/Users/kgtny/Documents/lingqu/core/tny-framework"
EXCLUDE_DIRS = {"build", ".gradle", "obsolete", "gradle", ".git", ".idea", "out", "node_modules", ".settings", "openspec", "docs"}

TOKEN_RE = re.compile(r"""
    (?P<ws>\s+)
  | (?P<lcomment>//[^\n]*)
  | (?P<comment>/\*.*?\*/)
  | (?P<str>"(?:\\.|[^"\\\n])*"|'(?:\\.|[^'\\\n])*')
  | (?P<num>0[xX][0-9a-fA-F_]+[lL]?|\d[\d_]*(?:\.\d[\d_]*)?(?:[eE][+-]?\d+)?[fFdDlLuU]?)
  | (?P<id>[a-zA-Z_$][\w$]*)
  | (?P<punct>::|->|\.\.\.|[{}()\[\];,.]|[+\-*/%&|^!~<>?]+|=+|:|@)
""", re.VERBOSE | re.DOTALL)

KEYWORDS = set("""abstract assert boolean break byte case catch char class const continue default do
double else enum extends final float for goto if implements import instanceof int interface long
native new package private protected public return short static strictfp super switch synchronized
this throw throws transient try void volatile while var record sealed yield true false null""".split())

def tokenize(text):
    toks, line = [], 1
    pos, n = 0, len(text)
    while pos < n:
        m = TOKEN_RE.match(text, pos)
        if not m:
            pos += 1
            continue
        kind, val = m.lastgroup, m.group()
        line += val.count("\n")
        if kind in ("str", "num"):
            toks.append(("LIT", line))
        elif kind in ("id", "punct"):
            toks.append((val, line))
        pos = m.end()
    return toks

def norm_struct(toks):
    out = []
    for t, l in toks:
        if t != "LIT" and t not in KEYWORDS and re.match(r"^[a-zA-Z_$]", t):
            t = "ID"
        out.append((t, l))
    return out

files = []
for dirpath, dirnames, filenames in os.walk(ROOT):
    dirnames[:] = [d for d in dirnames if d not in EXCLUDE_DIRS]
    files += [os.path.join(dirpath, f) for f in filenames if f.endswith(".java")]
files.sort()
sys.stderr.write(f"java files: {len(files)}\n")

raw = {}
for p in files:
    try:
        raw[p] = tokenize(open(p, encoding="utf-8").read())
    except Exception:
        pass

def rel(p):
    return os.path.relpath(p, ROOT)

def module_of(p):
    return rel(p).split(os.sep)[0]

def run_mode(name, window):
    seqs = {}
    g = 0
    offsets = []  # (cumstart, path)
    for p in files:
        t = raw.get(p)
        if not t:
            continue
        if name == "M3":
            t = norm_struct(t)
        if len(t) < window:
            continue
        seqs[p] = t
        offsets.append((g, p))
        g += len(t)
    toks_flat = [x for p in files if p in seqs for x in (tok for tok, _ in seqs[p])]
    # rolling hash
    prime, mask = 1000003, (1 << 61) - 1
    pw = pow(prime, window - 1, mask)
    vals, tv_cache = {}, {}
    def tv(i):
        v = tv_cache.get(i)
        if v is None:
            tok = toks_flat[i]
            w = vals.get(tok)
            if w is None:
                w = len(vals) + 7
                vals[tok] = w
            tv_cache[i] = w
            return w
        return v
    h2pos = defaultdict(list)
    h = 0
    for i in range(window):
        h = (h * prime + tv(i)) % mask
    h2pos[h].append(0)
    N = len(toks_flat)
    for i in range(1, N - window + 1):
        h = ((h - tv(i - 1) * pw) % mask * prime + tv(i + window - 1)) % mask
        h2pos[h].append(i)
    starts = [o for o, _ in offsets]
    def locate(gi):
        k = bisect.bisect_right(starts, gi) - 1
        return offsets[k][1], gi - offsets[k][0]
    clusters = []
    for poslist in h2pos.values():
        if len(poslist) < 2:
            continue
        regions = []  # (file, local_start, length_tokens)
        byfile = defaultdict(list)
        for gi in poslist:
            f, li = locate(gi)
            byfile[f].append(li)
        for f, ps in byfile.items():
            ps.sort()
            i = 0
            while i < len(ps):
                j = i
                while j + 1 < len(ps) and ps[j + 1] == ps[j] + 1:
                    j += 1
                regions.append((f, ps[i], window + (j - i)))
                i = j + 1
        if len(regions) >= 2:
            clusters.append(regions)
    # 富化: 行跨度
    out = []
    for regions in clusters:
        rich = []
        for f, s, ln in regions:
            t = seqs[f]
            l0 = t[s][1]
            l1 = t[min(s + ln - 1, len(t) - 1)][1]
            rich.append({"file": rel(f), "module": module_of(f), "line_start": l0,
                         "line_end": l1, "lines": l1 - l0 + 1, "tokens": ln})
        rich.sort(key=lambda r: -r["lines"])
        files_involved = {r["file"] for r in rich}
        maxl = rich[0]["lines"]
        total = sum(r["lines"] for r in rich)
        out.append({"regions": rich, "cross_file": len(files_involved) > 1,
                    "max_lines": maxl, "total_lines": total})
    out.sort(key=lambda c: -c["max_lines"])
    del seqs, toks_flat, tv_cache, h2pos
    return out

report = {}
for mode, w in (("M1", 80), ("M2", 80), ("M3", 140)):
    report[mode] = run_mode(mode, w)
    sys.stderr.write(f"{mode}: {len(report[mode])} clusters\n")

# 聚合: 模块被复制行数（每簇除"原件"外都计为冗余: total - max）
mod_redundant = defaultdict(int)
mod_clusters = defaultdict(int)
for mode, clusters in report.items():
    for c in clusters:
        red = c["total_lines"] - c["max_lines"]
        for r in c["regions"][1:]:
            mod_redundant[(mode, r["module"])] += r["lines"]
        mod_clusters[(mode, c["regions"][0]["module"])] += 1

with open("/tmp/dupreport.json", "w") as f:
    json.dump({m: cs for m, cs in report.items()}, f, ensure_ascii=False)

md = ["# Java 克隆扫描报告（CPD 风格，窗口=token 数 M1/M2=80 M3=140）\n"]
for mode in ("M1", "M2", "M3"):
    clusters = report[mode]
    cross = [c for c in clusters if c["cross_file"]]
    md.append(f"\n## {mode}：{len(clusters)} 簇（跨文件 {len(cross)}）\n")
    md.append("Top 20 簇（按最长区域行数；行号区间+文件）:\n")
    for i, c in enumerate(clusters[:20]):
        tag = "跨文件" if c["cross_file"] else "同文件"
        md.append(f"- [{tag}] max={c['max_lines']}行 total={c['total_lines']}行 regions={len(c['regions'])}")
        for r in c["regions"][:6]:
            md.append(f"    - {r['file']}:{r['line_start']}-{r['line_end']}")
    md.append(f"\n### {mode} 模块冗余行 Top 15（除原件外的副本行数）\n")
    rows = sorted(((m, v) for (mm, m), v in mod_redundant.items() if mm == mode), key=lambda x: -x[1])[:15]
    for m, v in rows:
        md.append(f"- {m}: {v}")
open("/tmp/dupreport.md", "w").write("\n".join(md))
sys.stderr.write("written /tmp/dupreport.md /tmp/dupreport.json\n")
