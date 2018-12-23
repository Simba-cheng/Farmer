<#--文件上传-弹窗-->
<div class="modal-dialog" role="document">
    <div class="modal-content">

        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="">
                <span aria-hidden="true">&times;</span>
            </button>
            <h4 class="modal-title" id="createCompleteNodelLabel">上传文件</h4>
        </div>

        <div class="modal-body">
            <div class="form-group">
                <label for="txt_departmentname">请填写父节点路径</label>
                <textarea id="upLoadFileNodePath" name="content" rows="3" cols="5" placeholder="请填写完整节点路径&#10;例如：/home/search/serch1"></textarea>
            </div>
        </div>

        <div class="container kv-main">
            <form enctype="multipart/form-data">
                <div class="form-group">
                    <input id="file-4" type="file" class="file" data-upload-url="#">
                </div>
            </form>
        </div>

    </div>
</div>