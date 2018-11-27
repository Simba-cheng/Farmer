<#--添加子节点弹窗-->
<div class="modal-dialog" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="zkIndex.closeAddChildNodePopup()">
                <span aria-hidden="true">&times;</span>
            </button>
            <h4 class="modal-title" id="addChildNodelLabel">添加节点</h4>
        </div>
        <div class="modal-body">
            <div class="form-group">
                <label for="txt_departmentname">添加子节点</label>
                <textarea id="addNodeChildPath" name="content" rows="3" cols="5" placeholder="节点名称（只需填写要添加的名称，不用写父节点）"></textarea>
            </div>
            <div class="form-group">
                <label for="txt_departmentname">节点中数据</label>
                <textarea id="addNodeChildData" name="content" rows="3" cols="5" placeholder="请填写节点数据"></textarea>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal" onclick="zkIndex.closeAddChildNodePopup()">
                <span class="glyphicon glyphicon-remove" aria-hidden="true"></span>关闭
            </button>
            <button type="button" id="btn_conn_zk_submit" class="btn btn-primary" data-dismiss="modal" onclick="zkIndex.addChildNode(this)">
                <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确定
            </button>
        </div>
    </div>
</div>
