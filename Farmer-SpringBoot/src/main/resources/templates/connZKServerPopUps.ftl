<#--连接zookeeper服务器弹窗页面-->
<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="zkIndex.connZkServerButtonClose()">
                <span aria-hidden="true">&times;</span>
            </button>
            <h4 class="modal-title" id="myModalLabel">连接ZooKeeper服务器</h4>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label for="txt_departmentname">ZooKeeper服务器 Host</label>
                <textarea id="txt_departmentname" name="content" rows="3" cols="5" placeholder="ip:port 或 ip:port,ip:port,ip:port"></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal" onclick="zkIndex.connZkServerButtonClose()">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>关闭
            </button>
        <#--点击保存的时候，也要调用connZkServerButtonClose-->
            <button type="button" id="btn_conn_zk_submit" class="btn btn-primary" data-dismiss="modal">
                <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>连接
            </button>
        </div>
    </div>
</div>
