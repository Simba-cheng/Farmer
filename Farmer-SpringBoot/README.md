# Farmer - ZooKeeper UI
Farmer是一个ZooKeeper的可视化工具，开箱即用。

如果使用中发现了bug，希望你能告诉我。

## 项目、工具概览

* 项目URL : http://localhost:9527/zk/index.do

* **首页展示图**<br>
![ZooKeeper-UI展示图](image/ZooKeeper-UI展示图.png)<br>

* **节点、节点数据展示图**<br>
![index页面节点-数据](image/index页面节点数据.png)<br>


## 项目简述
**项目定位** : 日常工具、开箱即用。

**用户体验** : 希望最好可以无脑使用

**项目技术** : JDK1.8、Java、SpringBoot 2.X、FreeMarker、JavaScript、jQuery


## 使用说明
从[installPackage目录](https://github.com/Simba-cheng/Farmer/tree/master/Farmer-SpringBoot/installPackage)中下载对应版本的jar包。
jar包的版本号，对应"版本计划"中的功能，高版本涵盖低版本的功能。


## 使用方法

* 1.**IDEA 编译器启动**<br>
    首先将项目导入IDEA中。<br>
    然后根据下图配置ZooKeeper服务器IP，多个地址用逗号(',')分隔。<br>
    ![IDEA编译器参数启动配置](image/IDEA编译器参数启动配置.png)
    <br>
    然后启动主类：com.server.FarmerApplication即可<br>
    然后访问：http://localhost:9527/zk/index.do<br>

    **上图不配置参数，程序也可以启动，点击页面的"连接ZooKeeper服务器"按钮，即可连接。**<br>
    ![ZooKeeper-UI展示图](image/index页面连接zookeeper服务器.png)


* 2.**命令行启动**<br>

    从[installPackage目录](https://github.com/Simba-cheng/Farmer/tree/master/Farmer-SpringBoot/installPackage)中下载对应版本的jar包。

    farmer-0.0.1.jar，可以将其放在桌面

    然后执行命令：java -jar farmer-0.0.1.jar zkClientHost=192.168.137.150:2181

    ![](image/命令行带参数启动.png)<br>

    **上图不配置参数，程序也可以启动，点击页面的"连接ZooKeeper服务器"按钮，即可连接。**<br>

* 3.**Linux服务器启动**(同上)


## 版本计划

### 0.0.1(已完成)

    1.命令行启动jar，通过追加'zkClientHost'参数，程序启动既初始化链接ZooKeeper服务器。
    2.命令行带'zkClientHost'参数启动,页面初始化即展示'/'(根节点)下的所有节点
    3.命令行启动jar，无'zkClientHost'参数，不影响程序启动，index页面展示。
    4.命令行无'zkClientHost'参数启动,index页面展示无节点数据。
    5.index页面，点击连接按钮，连接ZooKeeper服务器。
    6.index页面，点击刷新按钮，即可刷新页面。
    7.index页面，点击断开按钮，即断开ZooKeeper服务器连接。
    8.index页面，节点、子节点层级展示,节点与子节点层级动态关联。
    9.index页面，节点、子节点中的数据展示。
    10.index页面，文本展示区域显示行号。
    11.index页面，文本展示区域内容支持修改、保存数据
    12.index页面，后台异常，页面错误信息弹窗提示
    13.index页面，节点数据展示区域支持数据修改、保存。

### 0.0.2 (待定)

    1.节点展示区域支持鼠标右击，对节点进行增删查改。

### 0.0.3 (待定)
	1.展示ZooKeeper服务端相关参数、数据


## 注意事项：

**1.ZooKeeper服务器中，节点的命名，最好不要有"-"、"_"此类的标示符，否则会造成解析错误。**

    原因：页面节点属性class不能直接使用"/"、"."这些标示符，否则jQuery无法选择定位，所以，全部全换成了"-"和"_"。
    
**2.直接download下来的代码可能无法直接打包运行**

    原因：闲暇时间开发，可能会将尚未完成的功能的代码先提交。
    建议：直接使用installPackage目录中的安装包，详情见"使用说明"

**3.用户鉴权**

	Farmer定位是一个日常通用工具，没有增加用户鉴权。
	但随着后续版本迭代，可以成为基于ZooKeeper的配置中心可视化界面。
	所以预留了，用户鉴权接口