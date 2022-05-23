$(function () {
    function workflowList() {
        $("#workflow-list").empty();
        $('#loader').show();
        url = `/workflowList`;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (response) {
                $("#workflow-status").append(`<h3>${response.name}</h3>`);
                $("#workflow-status").append(`<h4>${statusCodeMap.get(response.status)}</h4>`);
                let k = response.length - 1;
                var html = [];
                //response.sort((a, b) => a.created - b.created);
                html.push('<ul class="list">');
                for (workflow of response) {
                    html.push(`<li class="item"> <a href = "/workflow/${workflow.id}">${workflow.id} :: ${workflow.name} :: ${statusCodeMap.get(workflow.status)}</a></li>`);
                }
                html.push('</ul>')
                $("#workflow-list").append(html.join(""));
                $('#loader').hide();
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }

    $("#createTaskSubmit").click(function (event) {
        event.preventDefault();
        $(".alert").remove();
        taskName = $("#taskName").val();
        serviceName = $("#serviceName").val();
        dockerImage = $("#dockerImage").val();
        cpuLimit = $("#cpuLimit").val();
        memoryLimit = $("#memoryLimit").val();
        nodePort = $("#nodePort").val();
        url = `/task/add`;

        data = {
            taskName: taskName,
            serviceName: serviceName,
            dockerImage: dockerImage,
            cpuLimit: cpuLimit,
            memoryLimit: memoryLimit,
            nodePort: parseInt(nodePort)
        }

        if(taskName != null && taskName.length > 0 && serviceName != null && serviceName.length > 0 &&
            dockerImage != null && dockerImage.length > 0){

            $.ajax({
                type: "POST",
                url: url,
                data: JSON.stringify(data),
                dataType: "json",
                contentType : 'application/json',
                encode: true,
                success: function (response) {
                    var msg = '<span class="alert alert-primary" role="alert" style="padding: 5px;">'
                                +'Task Created!'
                            +'</span>';

                    $('#addTask').find('#message').html(msg);
                    $('#addTask').find('#message').removeClass('hide');

                    // clear the create task form
                    $('div#addTask').find('input').each(function(ind, obj) {
                        $(obj).val('');
                    });
                },
                error: function (x, e) {
                    let errorMessage = "Error Occurred. Please check with the admin."
                    if(x.responseJSON && x.responseJSON.message) {
                        errorMessage = x.responseJSON.message
                    }
                    html = [];
                    html.push('<div id="create-error" class="alert alert-danger" role="alert">');
                    html.push(errorMessage);
                    html.push('</div>');
                    $("#addTask").prepend(html.join(""));
                    $("#create-error").focus();
                }
            });
        }
        else{
            var msg = '<span class="alert alert-primary" role="alert" style="padding: 5px;">'
                            +'Missing mandatory fields!'
                     +'</span>';

            $('#addTask').find('#message').html(msg);
            $('#addTask').find('#message').removeClass('hide');
        }
    });

});