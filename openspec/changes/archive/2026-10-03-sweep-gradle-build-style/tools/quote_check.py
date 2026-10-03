import re, sys, pathlib

def scan_dq(text, i):
    """从开引号位置 i（该字符为双引号）扫到真正的闭引号；${...} 内部的双引号不算闭合。
    返回 (闭引号索引, 引号间原文) 或 None（未闭合）。"""
    j = i + 1; n = len(text); depth = 0; start = i + 1
    while j < n:
        c = text[j]
        if depth == 0:
            if c == '\\': j += 2; continue
            if c == '"': return j, text[start:j]
            if c == '$' and j + 1 < n and text[j + 1] == '{': depth = 1; j += 2; continue
            j += 1; continue
        if c == '{': depth += 1
        elif c == '}': depth -= 1
        j += 1
    return None

# 报告 .gradle 文件代码区内"无插值仍用双引号"的字符串残留（跳过行注释与块注释）
def code_strings(text):
    out=[]; i=0; n=len(text); line=1; state='code'
    while i<n:
        c=text[i]
        if c=='\n': line+=1; i+=1; continue
        if state=='code':
            if text.startswith('//',i):
                while i<n and text[i]!='\n': i+=1
                continue
            if text.startswith('/*',i):
                state='block'; i+=2; continue
            if c=="'":
                i+=1
                while i<n and text[i]!="'": 
                    if text[i]=='\\': i+=1
                    i+=1
                i+=1; continue
            if text.startswith('"""', i) or text.startswith("'''", i):
                q = text[i:i+3]
                k = text.find(q, i+3)
                k = n if k < 0 else k+3
                line += text[i:k].count('\n'); i = k; continue
            if c=='"':
                r = scan_dq(text, i)
                if r is None:
                    i += 1; continue
                close_idx, body = r
                endline = line + text[i:close_idx+1].count('\n')
                if '$' not in body:
                    out.append((endline, '"'+body[:40]+'"'))
                line = endline
                i = close_idx + 1; continue
            i+=1; continue
        if state=='block':
            if text.startswith('*/',i): state='code'; i+=2; continue
            if c=='\n': line+=1
            i+=1; continue
    return out
for p in sys.argv[1:]:
    t=pathlib.Path(p).read_text()
    for ln,s in code_strings(t):
        print(f'{p}:{ln}: {s}')
