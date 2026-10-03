#!/usr/bin/env python3
"""把 dupreport.json 的重叠窗口簇合并成"克隆岛"并去重统计。
岛 = 区域(文件,行区间)有交叠的簇的传递闭包。
冗余行 = 岛内按文件合并区间后, 除最长一份外的行数(按模块归属)。
"""
import json
from collections import defaultdict

report = json.load(open("/tmp/dupreport.json"))

def interval_union(regions):
    """同文件区间合并"""
    byfile = defaultdict(list)
    for r in regions:
        byfile[r["file"]].append((r["line_start"], r["line_end"]))
    out = []
    for f, ivs in byfile.items():
        ivs.sort()
        cs, ce = ivs[0]
        for s, e in ivs[1:]:
            if s <= ce + 1:
                ce = max(ce, e)
            else:
                out.append((f, cs, ce)); cs, ce = s, e
        out.append((f, cs, ce))
    return out

def build_islands(clusters, min_lines):
    # 区域级并查集: 用文件+区间重叠建图(简单 O(n^2) 在过滤后规模可行)
    regions = []
    for ci, c in enumerate(clusters):
        for r in c["regions"]:
            if r["lines"] >= min_lines:
                regions.append((r, ci))
    # 岛屿聚类 via 簇索引连通(同簇区域直接连通; 区域交叠则合并其簇)
    parent = list(range(len(clusters)))
    def find(x):
        while parent[x] != x:
            parent[x] = parent[parent[x]]; x = parent[x]
        return x
    def union(a, b):
        ra, rb = find(a), find(b)
        if ra != rb: parent[rb] = ra
    byfile = defaultdict(list)
    for r, ci in regions:
        byfile[r["file"]].append((r["line_start"], r["line_end"], ci))
    for f, lst in byfile.items():
        lst.sort()
        for i in range(len(lst) - 1):
            # 同文件内排序后, 与后继若区间交叠即连通(仅相邻比较会漏长区间, 用扫描维护 max end)
            s, e, ci = lst[i]
            for j in range(i + 1, len(lst)):
                s2, e2, ci2 = lst[j]
                if s2 > e: break
                union(ci, ci2)
    islands = defaultdict(list)  # root -> regions
    for r, ci in regions:
        islands[find(ci)].append(r)
    out = []
    for regs in islands.values():
        merged = interval_union(regs)
        if len(merged) < 2:
            continue  # 同文件交叠自并——非克隆岛
        merged.sort(key=lambda x: -(x[2] - x[1] + 1))
        files = {m[0] for m in merged}
        total = sum(e - s + 1 for _, s, e in merged)
        redundant = sum(e - s + 1 for _, s, e in merged[1:])
        out.append({"regions": [{"file": f, "start": s, "end": e, "lines": e - s + 1} for f, s, e in merged],
                    "cross_file": len(files) > 1, "total_lines": total, "redundant_lines": redundant,
                    "max_lines": merged[0][2] - merged[0][1] + 1})
    out.sort(key=lambda i: -i["redundant_lines"])
    return out

md = ["# 克隆岛（重叠簇合并+去重后；最小区块 20 行）\n"]
for mode in ("M1", "M2", "M3"):
    islands = build_islands(report[mode], 20)
    md.append(f"\n## {mode}：{len(islands)} 岛（跨文件 {sum(1 for i in islands if i['cross_file'])}）"
              f"，冗余行合计 {sum(i['redundant_lines'] for i in islands):,}\n")
    mod_red = defaultdict(int)
    for i in islands:
        for r in i["regions"][1:]:
            mod_red[r["file"].split("/")[0]] += r["lines"]
    md.append("### 模块冗余行排行")
    for m, v in sorted(mod_red.items(), key=lambda x: -x[1])[:12]:
        md.append(f"- {m}: {v:,}")
    md.append("\n### Top 25 岛")
    for i in islands[:25]:
        tag = "跨文件" if i["cross_file"] else "同文件"
        md.append(f"- [{tag}] 冗余{i['redundant_lines']}行 原件{i['max_lines']}行")
        for r in i["regions"]:
            md.append(f"    - {r['file']}:{r['start']}-{r['end']} ({r['lines']}行)")
open("/tmp/dupislands.md", "w").write("\n".join(md))
print("ok")
