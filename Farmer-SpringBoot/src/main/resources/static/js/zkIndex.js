var clickNodePath = "";

var zkIndex = {

    //初始化
    init: function () {
        this.indexQuery();
        this.connZkServer();
        this.closeZkServer();
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
    connZkServer: function () {
        $("#btn_conn_zk_submit").click(function () {

            var zkConnHost = $("#txt_departmentname").val();

            $.ajax({
                type: "post",
                dataType: 'json',
                async: false,
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
                }
            });
            zkIndex.connZkServerButtonClose();
        });
    },

    //连接服务器按钮-打开弹窗事件
    connZkServerButton: function () {
        $("#button_conn_zkServer").click(function (e) {

            // $("#myModalLabel").text("连接ZooKeeper服务器");
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
        //还原css样式
        $("#myModal").css("opacity", "0");
        $("#myModal").css("top", "-25%");

        //清空文本框中的内容
        $("#txt_departmentname").val("");
    },

    //断开ZooKeeper服务器连接
    closeZkServer: function () {
        $("#button_close_zkServer").click(function () {

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
        });
    },

    //点击节点触发的事件
    nodeInfoQuery: function (e) {
        var nodePath = e.id;
        var thisNode = $("#" + nodePath);
        var titleValue = $("#" + nodePath).attr("title");
        var nodeisfilevalue = $("#" + nodePath).attr("nodeisfilevalue");

        //每次点击时，将id存储到 变量'clickNodePath'中，如果修改数据,则使用这个变量
        clickNodePath = nodePath;

        if ("Expand this branch" == titleValue) {
            // 'Expand this branch'展开此分支

            // 1.查询节点中的数据内容
            this.queryNodeInfoData(nodePath);

            // 2.查询该节点的子节点 & 拼接
            this.queryChildNode(e);

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
        var name = $("#" + nodePath).attr("name");
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
                        $("#node_data_name").html(name);

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
    queryChildNode: function (e) {

        var name = $("#" + e.id).attr("name");
        var nodeClass = e.className;
        var inputData = {"nodePath": e.id};

        $.ajax({
            type: "post",
            dataType: "json",
            async: false,
            url: "/zk/queryChildNodeList.do",
            data: {"inputData": JSON.stringify(inputData)},
            success: function (data) {
                var resultData = data.resultData;
                if ("Y" == resultData.isSuccess) {

                    var childNodes = resultData.childNodeInfoList;
                    var resultHTMLData = "<ul>";
                    var htmlData = "";
                    if ("" != childNodes && undefined != childNodes) {
                        for (var i = 0; i < childNodes.length; i++) {

                            var childNode = childNodes[i];
                            var nodePath = childNode.nodePath;
                            var completeNode = childNode.completeNode;

                            //节点是否是文件形式 1-是，0-不是
                            var nodeIsFileValue = childNode.nodeIsFile;
                            var iconStr = "";
                            if ("1" == nodeIsFileValue) {
                                // iconStr = "icon-leaf";
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

                        if ("" != htmlData && undefined != htmlData) {
                            var htmlNodeData = resultHTMLData + htmlData + "</ul>";
                            var thisNode = $("#" + e.id);
                            thisNode.next().remove();
                            thisNode.after(htmlNodeData);
                        }
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
        if (childNodeName.indexOf(".") != -1) {
            childNodeName = childNodeName.replace(".", "-");
        }

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
                    $("#" + nodeID).parent().attr("class", "parent_li");

                    //刷新节点
                    zkIndex.createNodeRefreshNode(parentNode);
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
                            $("#" + nodePath).parent().remove();

                        } else if ("N" == result.isSuccess) {
                            var error = result.errorInfo;
                            sweetAlert("异常信息", error.errorMessage, "error");
                        }
                        //刷新被删除节点的父节点
                        // zkIndex.deleteNodeRefreshNode(nodePath);
                    }
                });
            });
    },

    // //删除选中节点(包括选中节点下的所有子节点)
    // deleteAllChildNode: function (e) {
    //
    //     var nodePath = e[0].id;
    //
    //     //弹窗提醒
    //     swal({
    //             title: "确定全部删除吗？",
    //             text: "确定全部删除？该节点下子节点也会全部删除！",
    //             type: "warning",
    //             showCancelButton: true,
    //             confirmButtonColor: "#DD6B55",
    //             cancelButtonText: "取消删除！",
    //             confirmButtonText: "确定全部删除！",
    //             closeOnConfirm: false
    //         },
    //         function () {
    //             $.ajax({
    //                 type: "post",
    //                 dataType: "json",
    //                 async: false,
    //                 url: "/zk/deleteAllChildNodes.do",
    //                 data: {"nodePath": nodePath},
    //                 success: function (data) {
    //
    //                     var result = data.resultData;
    //
    //                     if ("Y" == result.isSuccess) {
    //                         var displayCopy = result.displayCopy + " " + result.nodePath;
    //                         sweetAlert("删除成功", displayCopy, "success")
    //
    //                         //刷新删除的节点信息
    //                         // span 当前节点 父节点 li 删除
    //                         $("#" + nodePath).parent().remove();
    //
    //                     } else if ("N" == result.isSuccess) {
    //                         var error = result.errorInfo;
    //                         sweetAlert("异常信息", error.errorMessage, "error");
    //                     }
    //                 }
    //             });
    //         });
    // },

    //刷新节点-删除节点后刷新节点用于展示
    // deleteNodeRefreshNode: function (nodeID) {
    //
    //     //被点击的节点
    //     var node = $("#" + nodeID);
    //     //找到节点层级关系中的父节点
    //     var parentNode = node.parent().parent().prev();
    //     var parentNodeID = parentNode[0].id;
    //
    //     var inputData = {"nodePath": parentNodeID};
    //     $.ajax({
    //         type: "post",
    //         dataType: "json",
    //         async: false,
    //         url: "/zk/queryChildNodeList.do",
    //         data: {"inputData": JSON.stringify(inputData)},
    //         success: function (data) {
    //             var resultData = data.resultData;
    //             if ("Y" == resultData.isSuccess) {
    //
    //                 var childNodes = resultData.childNodeInfoList;
    //                 var resultHTMLData = "<ul>";
    //                 var htmlData = "";
    //                 for (var i = 0; i < childNodes.length; i++) {
    //
    //                     var childNode = childNodes[i];
    //                     var nodePath = childNode.nodePath;
    //                     var completeNode = childNode.completeNode;
    //
    //                     //节点是否是文件形式 1-是，0-不是
    //                     var nodeIsFileValue = childNode.nodeIsFile;
    //                     var iconStr = "";
    //                     if ("1" == nodeIsFileValue) {
    //                         iconStr = "icon-file";
    //                     } else if ("0" == nodeIsFileValue) {
    //                         iconStr = "icon-folder-open";
    //                     }
    //
    //                     var isExistenceChildClass = "";
    //                     if ("1" == childNode.isExistenceChild) {
    //                         isExistenceChildClass = "parent_li";
    //                     }
    //
    //                     htmlData +=
    //                         "<li class='" + isExistenceChildClass + "'>" +
    //                         "<span class='clickNodeMark' name='" + nodePath + "' id='" + completeNode + "' nodeIsFileValue='" + nodeIsFileValue + "' onclick='zkIndex.nodeInfoQuery(this)' title='Expand this branch'>" +
    //                         "<i class='" + iconStr + "'></i>" +
    //                         nodePath +
    //                         "</span>" +
    //                         "</li>";
    //                 }
    //
    //                 if ("" == htmlData) {
    //                     var thisNode = $("#" + parentNodeID);
    //                     //span 节点的兄弟节点 ul
    //                     thisNode.next().remove();
    //                 } else if (undefined != htmlData) {
    //                     var htmlNodeData = resultHTMLData + htmlData + "</ul>";
    //                     var thisNode = $("#" + parentNodeID);
    //                     //span 节点的兄弟节点 ul
    //                     thisNode.next().remove();
    //                     thisNode.after(htmlNodeData);
    //                 }
    //             } else if ("N" == resultData.isSuccess) {
    //                 var errorMessage = resultData.errorInfo.errorMessage;
    //                 sweetAlert("异常信息", errorMessage, "error");
    //             }
    //         }
    //     });
    // },

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

                    if (undefined != htmlData) {
                        var htmlNodeData = resultHTMLData + htmlData + "</ul>";
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

    //渲染节点数据展示区域
    renderingNodeDataInfo: function () {
        $("#node-info-display-input").setTextareaCount({
            width: "30px",
            bgColor: "#000",
            color: "#FFF",
            display: "inline-block"
        });
    }
};

$(function () {
    zkIndex.init();
});