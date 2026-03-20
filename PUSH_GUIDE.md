# 为什么远程仓库看不到新文件

你当前环境里只有本地提交，且没有配置 `origin` 远程地址，所以提交不会自动出现在 GitHub。

## 现象
- 本地分支：`work`
- 远程页面看的分支：`main`

## 正确推送步骤
```bash
git remote add origin https://github.com/Kiluy/xiuninvyou.git
# 如果已存在 origin，用：git remote set-url origin https://github.com/Kiluy/xiuninvyou.git

git push -u origin work
```

如果你希望直接在 GitHub `main` 分支看到：
```bash
git push -u origin work:main
```
