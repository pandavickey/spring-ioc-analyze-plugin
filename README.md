# spring-ioc-analyze-plugin
A Intellij IDEA plugin for spring IOC dependency analyze.

提供给 Spring 项目排查 Java 循环依赖问题的 Intellij 插件。

# 安装方式

1、通过本地安装的方式直接安装 product/spring-ioc-analyze-plugin 文件。（Intellij IDEA 插件本地安装路径: Preferences -> Plugins -> Settings -> Install Plugin from Disk 。)

2、在插件市场搜索 spring-ioc-analyze 直接安装。

# 使用方式

1、 在 Intellij 需要分析的工程、项目或者文件目录中右击弹出菜单栏选择 【SpringIOCAnalyze】：

![菜单](product/screen-select.png)


2、 等待分析结果，内容如下显示:

![结果](product/screen-result.png)