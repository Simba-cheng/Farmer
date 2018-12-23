var clickNodePath = "";

var zkIndex = {

    //初始化
    init: function () {
        this.indexQuery();
        this.connZkServer();
        this.closeZkServerClick();
        this.connZkServerButton();
        this.refreshPage();
        this.submitNodeData();
    },

    //初始化查询-默认展示第一层节点
    indexQuery: function () {
        var inputData = {"nodePath": "/"};
        $.ajax({
            type: "get",
            dataType: 'json',
            async: false,
            url: "/zk/indexQuery.do",
            data: {"inputData": JSON.stringify(inputData)},
            success: function (data) {
                if ("Y" == data.code) {
                    var htmlData = data.html;
                    $('#node_start').html(htmlData);
                } else if ("N" == data.code) {
                    var errorMessage = data.errorMessage;
                    sweetAlert("异常信息", errorMessage, "error");
                }
            }
        });
    },

    //连接服务器
    //异步-10秒钟超时时间，10秒钟没有连接上，认为是ZooKeeper服务器Host有问题。
    //底层客户端连接时间设置为Integer.MAX,是为了尽量避免session失效等连接后产生的问题。
    connZkServer: function () {
        $("#btn_conn_zk_submit").click(function () {
            var zkConnHost = $("#txt_departmentname").val();
            $.ajax({
                type: "post",
                dataType: 'json',
                async: true,
                timeout: 10000,
                url: "/zk/conn.do",
                data: {"host": zkConnHost},
                success: function (data) {
                    var result = data.resultData;
                    if ("Y" == result.isSuccess) {
                        sweetAlert("连接成功", "已连接", "success")
                        zkIndex.indexQuery();
                    } else if ("N" == result.isSuccess) {
                        var errorMessage = result.errorInfo.errorMessage;
                        sweetAlert("异常信息", errorMessage, "error");
                    }
                    //清空文本框中的内容
                    $("#txt_departmentname").val("");
                },
                complete: function (XMLHttpRequest, status) {
                    //超时处理
                    if (status == 'timeout') {
                        $.ajax({
                            type: "post",
                            dataType: 'json',
                            async: false,
                            url: "/zk/closeZooKeeperServerConn.do",
                            success: function (data) {
                            }
                        });
                        sweetAlert("异常信息", "超过默认连接时间(10S),请检查host是否填写正确", "error");
                    }
                }
            });
            zkIndex.connZkServerButtonClose();
        });
    },

    //连接服务器按钮-打开弹窗事件
    connZkServerButton: function () {
        $("#button_conn_zkServer").click(function (e) {
            $("#myModal").modal();
            //修改CSS属性
            var opacityValue = $("#myModal").css("opacity");
            if ("0" == opacityValue) {
                $("#myModal").css("opacity", "1");
            }
            $("#myModal").css("top", "50%");
        });
    },

    // 刷新页面
    refreshPage: function () {
        $("#button_refresh_page").click(function () {
            zkIndex.indexQuery();
            //隐藏数据展示区域
            $("#node-info-display").hide();
        });
    },

    //连接服务器按钮-关闭弹窗事件
    connZkServerButtonClose: function () {
        $("#myModal").modal('hide');
        //还原css样式
        $("#myModal").css("opacity", "0");
        $("#myModal").css("top", "-25%");
        //清空文本框中的内容
        $("#txt_departmentname").val("");
    },

    //断开ZooKeeper服务器连接
    closeZkServerClick: function () {
        $("#button_close_zkServer").click(function () {
            //弹窗提醒
            swal({
                    title: "警告！",
                    text: "确定关闭连接吗？",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    cancelButtonText: "取消关闭",
                    confirmButtonText: "确定关闭！",
                    closeOnConfirm: false
                },
                function () {
                    zkIndex.closeZkServer();
                });
        });
    },

    //关闭连接
    closeZkServer: function () {
        $.ajax({
            type: "post",
            dataType: 'json',
            async: false,
            url: "/zk/closeZooKeeperServerConn.do",
            success: function (data) {
                var result = data.resultData;
                if ("Y" == result.isSuccess) {
                    sweetAlert("断开连接", result.displayCopy, "success")
                } else {
                    var errorInfo = result.errorInfo;
                    sweetAlert("异常信息", errorInfo.errorMessage, "error");
                }
            }
        });
    },

    //点击节点触发的事件
    nodeInfoQuery: function (e) {

        //原始路径
        var nodePath = e.id;
        //特殊字符转换
        var forMatNodePath = zkIndex.escapeJquery(nodePath);
        var thisNode = $("#" + forMatNodePath);
        var titleValue = $("#" + forMatNodePath).attr("title");
        var nodeisfilevalue = $("#" + forMatNodePath).attr("nodeisfilevalue");

        //每次点击时，将id存储到 变量'clickNodePath'中，如果修改数据,则使用这个变量
        clickNodePath = nodePath;

        //文件形式节点不用打开和展开，所以直接查询
        if ("1" == nodeisfilevalue || "Expand this branch" == titleValue) {
            // 'Expand this branch'展开此分支

            // 1.查询节点中的数据内容
            this.queryNodeInfoData(nodePath);
            // 2.查询该节点的子节点 & 拼接
            this.queryChildNode(nodePath, forMatNodePath);
            // 3.将此分支title设置为'Collapse this branch'
            thisNode.attr("title", "Collapse this branch");

        } else if ("Collapse this branch" == titleValue) {
            // 'Collapse this branch' 收起此分支

            //================================
            var children = thisNode.parent('li.parent_li').find(' > ul > li');
            if (children.is(":visible")) {
                children.hide('fast');
                children.attr('title', 'Expand this branch').find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
            } else {
                children.show('fast');
                children.attr('title', 'Collapse this branch').find(' > i').addClass('icon-minus-sign').removeClass('icon-plus-sign');
            }
            //================================

            //收起操作完成后，将title改为'Expand this branch'
            thisNode.attr("title", "Expand this branch");
            //文件夹形式的节点隐藏数据展示区域，文件形式的节点不隐藏
            if ("0" == nodeisfilevalue) {
                //隐藏节点数据展示区域
                $("#node-info-display").hide();
            }
        } else {
            //查询节点数据
            this.queryNodeInfoData(nodePath);
        }
    },

    //查询节点中的内容
    queryNodeInfoData: function (nodePath) {
        var inputData = {"nodePath": nodePath};
        $.ajax({
            type: "post",
            dataType: "json",
            async: false,
            url: "/zk/queryNodeInfoData.do",
            data: {"inputData": JSON.stringify(inputData)},
            success: function (data) {
                var resultData = data.resultData;
                if ("Y" == resultData.isSuccess) {
                    var nodeData = resultData.data;
                    if (undefined != nodeData) {
                        //展示文本框
                        $("#node-info-display").show();
                        //展示文本框行号渲染
                        zkIndex.renderingNodeDataInfo();
                        //展示文本框上方显示节点名称
                        $("#node_data_name").show();
                        $("#node_data_name").html(nodePath);
                        //塞入节点数据内容
                        $("#node-info-display-input").val(nodeData);
                    }
                } else if ("N" == resultData.isSuccess) {
                    var errorMessage = resultData.errorInfo.errorMessage;
                    sweetAlert("异常信息", errorMessage, "error");
                }
            }
        });
    },

    //查询子节点
    queryChildNode: function (nodePath, forMatNodePath) {
        var inputData = {"nodePath": nodePath};
        $.ajax({
            type: "post",
            dataType: "json",
            async: false,
            url: "/zk/queryChildNodeList.do",
            data: {"inputData": JSON.stringify(inputData)},
            success: function (data) {
                var resultData = data.resultData;
                if ("Y" == resultData.isSuccess) {
                    //节点拼接
                    var resultHTMLData = zkIndex.splicingNodeContent(resultData);
                    if ("" != resultHTMLData && undefined != resultHTMLData) {
                        // var thisNode = zkIndex.escapeJquery(nodePath);
                        var thisNode = $("#" + forMatNodePath);
                        thisNode.next().remove();
                        thisNode.after(resultHTMLData);
                    }
                } else if ("N" == resultData.isSuccess) {
                    var errorMessage = resultData.errorInfo.errorMessage;
                    sweetAlert("异常信息", errorMessage, "error");
                }
            }
        });
    },

    //数据展示区域-确定修改按钮
    submitNodeData: function () {

        $("#nodeData_SubmitButton").click(function (e) {
            var nodePath = clickNodePath;
            //获取文本编辑框中的数据
            var data = $("#node-info-display-input").val();
            var inputData = {"nodePath": nodePath, "nodeData": data};

            $.ajax({
                type: "post",
                dataType: "json",
                async: false,
                url: "/zk/setDataForNode.do",
                data: {"inputData": JSON.stringify(inputData)},
                success: function (data) {
                    var result = data.resultData;
                    if ("Y" == result.isSuccess) {
                        sweetAlert("更新数据", result.displayCopy, "success")
                    } else {
                        var errorMessage = result.errorInfo.errorMessage;
                        sweetAlert("异常信息", errorMessage, "error");
                    }
                    zkIndex.queryNodeInfoData(nodePath);
                }
            });
        });
    },

    //添加子节点
    addChildNode: function (e) {

        //被点击的节点
        var parentNode = $("#addNodeChildPath").attr("nodePath");
        //填写的需要创建的节点名称
        var childNodeName = $("#addNodeChildPath").val();
        var nodeData = $("#addNodeChildData").val();

        $.ajax({
            type: "post",
            dataType: "json",
            async: false,
            url: "/zk/addChildNode.do",
            data: {"parentNode": parentNode, "childNode": childNodeName, "nodeData": nodeData},
            success: function (data) {
                var result = data.resultData;
                if ("Y" == result.isSuccess) {
                    var info = result.displayCopy + " " + result.nodePath;
                    sweetAlert("创建成功", info, "success")

                    //当前节点的li设置class属性
                    var nodeID = parentNode;
                    nodeID = zkIndex.escapeJquery(nodeID);
                    $("#" + nodeID).parent().attr("class", "parent_li");

                    //刷新节点
                    zkIndex.createNodeRefreshNode(parentNode);
                } else {
                    var errorMessage = result.errorInfo.errorMessage;
                    sweetAlert("异常信息", errorMessage, "error")
                }
            }
        });
        //将编辑区域内容清空
        $("#addNodeChildPath").val("");
        $("#addNodeChildData").val("");
    },

    //右击-打开添加子节点弹窗
    addChildNodePopup: function (e) {

        //被点击的节点
        var nodePath = e[0].id;
        //将被点击的节点，存储到编辑区节点的属性中
        $("#addNodeChildPath").attr("nodePath", nodePath);
        $("#addChildNodelLabel").text("添加" + nodePath + "的子节点");

        // 弹出弹框
        $("#addChildNodeParent").modal();
        //修改CSS属性
        var opacityValue = $("#addChildNodeParent").css("opacity");
        if ("0" == opacityValue) {
            $("#addChildNodeParent").css("opacity", "1");
        }
        $("#addChildNodeParent").css("top", "50%");
    },

    //关闭-添加子节点弹窗展示
    closeAddChildNodePopup: function () {

        $("#addChildNodeParent").modal('hide');

        //还原css样式
        $("#addChildNodeParent").css("opacity", "0");
        $("#addChildNodeParent").css("top", "-25%");

        //编辑区内容清空
        $("#addNodeChildPath").val("");
        $("#addNodeChildData").val("");
    },

    //删除选中节点
    deleteNode: function (e) {

        //被选点击的节点id,节点全路径
        var nodePath = e[0].id;
        var formatNodePath = zkIndex.escapeJquery(nodePath);

        //弹窗提醒
        swal({
                title: "确定删除吗？",
                text: "确定删除此节点嘛？如果该节点下还有子节点则无法删除",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                cancelButtonText: "取消删除！",
                confirmButtonText: "确定删除！",
                closeOnConfirm: false
            },
            function () {
                $.ajax({
                    type: "post",
                    dataType: "json",
                    async: false,
                    url: "/zk/deleteChildNode.do",
                    data: {"nodePath": nodePath},
                    success: function (data) {
                        var result = data.resultData;
                        if ("Y" == result.isSuccess) {
                            var displayCopy = result.displayCopy + " " + result.nodePath;
                            sweetAlert("删除成功", displayCopy, "success")
                            //删除页面中的节点
                            $("#" + formatNodePath).parent().remove();
                            // 隐藏数据展示区域
                            $("#node-info-display").hide();
                        } else if ("N" == result.isSuccess) {
                            var error = result.errorInfo;
                            sweetAlert("异常信息", error.errorMessage, "error");
                        }
                    }
                });
            });
    },

    //删除选中节点(包括选中节点下的所有子节点)
    deleteAllChildNode: function (e) {

        var nodePath = e[0].id;
        var formatNodePath = zkIndex.escapeJquery(nodePath);

        //弹窗提醒
        swal({
                title: "确定全部删除吗？",
                text: "确定全部删除？该节点下子节点也会全部删除！",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                cancelButtonText: "取消删除！",
                confirmButtonText: "确定全部删除！",
                closeOnConfirm: false
            },
            function () {
                $.ajax({
                    type: "post",
                    dataType: "json",
                    async: false,
                    url: "/zk/deleteAllChildNodes.do",
                    data: {"nodePath": nodePath},
                    success: function (data) {

                        var result = data.resultData;
                        if ("Y" == result.isSuccess) {
                            var displayCopy = result.displayCopy + " " + result.nodePath;
                            sweetAlert("删除成功,请手动刷新页面", displayCopy, "success")

                            // span 当前节点 父节点 li 删除
                            $("#" + formatNodePath).parent().remove();
                            // 隐藏数据展示区域
                            $("#node-info-display").hide();
                        } else if ("N" == result.isSuccess) {
                            var error = result.errorInfo;
                            sweetAlert("异常信息", error.errorMessage, "error");
                        }
                    }
                });
            });
    },

    //创建节点后，刷新当前节点，重新展示节点信息
    createNodeRefreshNode: function (nodeID) {
        var inputData = {"nodePath": nodeID};
        $.ajax({
            type: "post",
            dataType: "json",
            async: false,
            url: "/zk/queryChildNodeList.do",
            data: {"inputData": JSON.stringify(inputData)},
            success: function (data) {
                var resultData = data.resultData;
                if ("Y" == resultData.isSuccess) {

                    //拼接节点信息
                    var htmlNodeData = zkIndex.splicingNodeContent(resultData);

                    if (undefined != htmlNodeData) {
                        nodeID = zkIndex.escapeJquery(nodeID);
                        var thisNode = $("#" + nodeID);
                        //span 节点的兄弟节点 ul
                        thisNode.next().remove();
                        thisNode.after(htmlNodeData);
                    }
                } else if ("N" == resultData.isSuccess) {
                    var errorMessage = resultData.errorInfo.errorMessage;
                    sweetAlert("异常信息", errorMessage, "error");
                }
            }
        });
    },

    //拼接节点内容
    splicingNodeContent: function (resultData) {

        var childNodes = resultData.childNodeInfoList;
        var resultHTMLData = "<ul>";
        var htmlData = "";
        for (var i = 0; i < childNodes.length; i++) {

            var childNode = childNodes[i];
            var nodePath = childNode.nodePath;
            var completeNode = childNode.completeNode;

            //节点是否是文件形式 1-是，0-不是
            var nodeIsFileValue = childNode.nodeIsFile;
            var iconStr = "";
            if ("1" == nodeIsFileValue) {
                iconStr = "icon-file";
            } else if ("0" == nodeIsFileValue) {
                iconStr = "icon-folder-open";
            }

            var isExistenceChildClass = "";
            if ("1" == childNode.isExistenceChild) {
                isExistenceChildClass = "parent_li";
            }

            htmlData +=
                "<li class='" + isExistenceChildClass + "'>" +
                "<span class='clickNodeMark' name='" + nodePath + "' id='" + completeNode + "' nodeIsFileValue='" + nodeIsFileValue + "' onclick='zkIndex.nodeInfoQuery(this)' title='Expand this branch'>" +
                "<i class='" + iconStr + "'></i>" +
                nodePath +
                "</span>" +
                "</li>";
        }

        var htmlNodeData = resultHTMLData + htmlData + "</ul>";

        return htmlNodeData;
    },

    //新建节点-弹窗展示
    popUpsCreateCompleteNodePath: function () {
        $("#addAllNodePath").modal();

        //修改CSS属性
        var opacityValue = $("#addAllNodePath").css("opacity");
        if ("0" == opacityValue) {
            $("#addAllNodePath").css("opacity", "1");
        }
        $("#addAllNodePath").css("top", "50%");
    },

    //新建节点-关闭弹窗
    closePopUpsCreateCompleteNodePath: function () {

        $("#addAllNodePath").modal('hide');

        //还原css样式
        $("#addAllNodePath").css("opacity", "0");
        $("#addAllNodePath").css("top", "-50%");

        //清空数据
        $("#createCompleteNodePath").val("");
        $("#createCompleteNodeData").val("");
    },

    //新建节点-创建完整节点路径
    createCompleteNodePath: function () {

        //完整节点路径
        var createCompleteNodePath = $("#createCompleteNodePath").val();
        //完整节点路径的数据
        var createCompleteNodeData = $("#createCompleteNodeData").val();

        if (undefined != createCompleteNodePath && "" != createCompleteNodePath && createCompleteNodePath.length > 0) {

            var root = createCompleteNodePath[0];

            if ("/" != root) {
                sweetAlert("ERROR:请输入完整节点路径", "请从根节点开始(例如：/home/test)", "error");
                $("#createCompleteNodePath").val("");
                $("#createCompleteNodeData").val("");
                return;
            }

            $.ajax({
                type: "post",
                dataType: "json",
                async: false,
                url: "/zk/creatCompleteNodes.do",
                data: {"nodePath": createCompleteNodePath, "nodeData": createCompleteNodeData},
                success: function (data) {

                    console.log(data);

                    var resultData = data.resultData;
                    if ("Y" == resultData.isSuccess) {

                        sweetAlert("创建成功", "创建节点 " + resultData.nodePath + " 成功，请手动刷新页面", "success")

                        //清空数据
                        $("#createCompleteNodePath").val("");
                        $("#createCompleteNodeData").val("");

                    } else {
                        var errorMessage = resultData.errorInfo.errorMessage;
                        sweetAlert("异常信息", errorMessage, "error");

                        //清空数据
                        $("#createCompleteNodePath").val("");
                        $("#createCompleteNodeData").val("");
                    }
                }
            });
        } else {
            sweetAlert("异常信息", "请输入正确的节点路径", "error");
            $("#createCompleteNodePath").val("");
            $("#createCompleteNodeData").val("");
        }
    },

    //文件上传弹窗展示
    fileUpLoadPopUps: function () {

        $("#upLoadFilePopUps").modal();

        //修改CSS属性
        var opacityValue = $("#upLoadFilePopUps").css("opacity");
        if ("0" == opacityValue) {
            $("#upLoadFilePopUps").css("opacity", "1");
        }
        $("#upLoadFilePopUps").css("top", "50%");
    },

    //文件上传关闭弹窗
    closeFileUpLoadPopUps: function () {

        $("#upLoadFilePopUps").modal('hide');

    },

    //渲染节点数据展示区域
    renderingNodeDataInfo: function () {
        $("#node-info-display-input").setTextareaCount({
            width: "30px",
            bgColor: "#000",
            color: "#FFF",
            display: "inline-block"
        });
    },

    /**
     * 特殊字符转义
     *
     * zookeeper节点中包含'/'(斜杠)，为了方便使用jQuery定位，在id中直接拼接完整的节点路径
     *
     * 但在jquery、js中，'/'(斜杠)是特殊字符，使用选择器无法定位；
     * 同时，为了防止用户定义节点名称时使用其他特殊字符，所以对节点路径、名称进行转义
     *
     * */
    escapeJquery: function (srcString) {

        // 转义之后的结果
        var escapseResult = srcString;

        // javascript正则表达式中的特殊字符
        // var jsSpecialChars = ["\\", "^", "$", "*", "?", ".", "+", "(", ")", "[",
        //     "]", "|", "{", "}"];

        // jquery中的特殊字符,不是正则表达式中的特殊字符
        var jquerySpecialChars = ["~", "`", "@", "#", "%", "&", "=", "'", "\"",
            ":", ";", "<", ">", ",", "/"];

        // for (var i = 0; i < jsSpecialChars.length; i++) {
        //     escapseResult = escapseResult.replace(new RegExp("\\" + jsSpecialChars[i], "g"), "\\" + jsSpecialChars[i]);
        // }

        for (var i = 0; i < jquerySpecialChars.length; i++) {
            escapseResult = escapseResult.replace(new RegExp(jquerySpecialChars[i], "g"), "\\" + jquerySpecialChars[i]);
        }

        return escapseResult;
    }
};

$(function () {
    zkIndex.init();
});
