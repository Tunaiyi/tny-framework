import sys, pathlib

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

# 把 .gradle 代码区中"无插值、无单引号、无转义双引号"的双引号字符串转为单引号（跳过注释）
def fix(text):
    out=[]; i=0; n=len(text); state='code'; changed=0
    while i<n:
        c=text[i]
        if state=='code':
            if text.startswith('//',i):
                j=i
                while j<n and text[j]!='\n': j+=1
                out.append(text[i:j]); i=j; continue
            if text.startswith('/*',i):
                j=text.find('*/',i)
                j=n if j<0 else j+2
                out.append(text[i:j]); i=j; continue
            if c=="'":
                j=i+1
                while j<n and text[j]!="'":
                    if text[j]=='\\': j+=1
                    j+=1
                out.append(text[i:j+1]); i=j+1; continue
            if text.startswith('"""', i) or text.startswith("'''", i):
                q = text[i:i+3]
                k = text.find(q, i+3)
                k = n if k < 0 else k+3
                out.append(text[i:k]); i = k; continue
            if c=='"':
                r = scan_dq(text, i)
                if r is None:
                    out.append(c); i += 1; continue
                close_idx, body = r
                if '$' not in body and "'" not in body and '\\"' not in body:
                    out.append("'"+body+"'"); changed += 1
                else:
                    out.append(text[i:close_idx+1])
                i = close_idx + 1
                continue
            out.append(c); i+=1; continue
        out.append(c); i+=1
        if text.startswith('*/',i-1): state='code'; out.append(''); 
        elif c=='\n': pass
    return ''.join(out), changed
total=0
for p in sys.argv[1:]:
    path=pathlib.Path(p)
    t=path.read_text()
    new,ch=fix(t)
    if ch:
        path.write_text(new)
        print(f'{p}: {ch} strings converted')
        total+=ch
print(f'TOTAL {total}')
