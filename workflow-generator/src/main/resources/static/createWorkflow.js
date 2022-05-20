$(function () {
    var allTasks = [];
    var nextValue = 2;

    function retrieveTasks() {
        url = '/task/names';
        $.ajax({
            url: url,
            type: 'GET',
            async: false,
            success: function (response) {
                allTasks = response;
            },
            error: function (x, e) {
                console.log(e);
            }
        });
    }
    retrieveTasks();
    $(".dropdown-menu").empty();
    for(task of allTasks) {
        $(".dropdown-menu").append(`<li class="dropdown-item" data-taskId="${task.id}">${task.taskName}</li>`);
    }

    $(document).on('click', '.dropdown-menu li', function() {
        const selectedElement = $(this);
        parentDropdownMenu = selectedElement.parent();
        selectedValueButton = parentDropdownMenu.siblings(".selectedValue");
        selectedValueButton.html(selectedElement.html());
        selectedValueButton.attr("data-taskId", selectedElement.attr("data-taskId"));
    });

     $('#addTaskToWorkflow').on('click', function() {
        html=[];
        html.push('<div class="dropdown">');
        html.push(`<button class="btn btn-secondary dropdown-toggle selectedValue" type="button" data-taskId= "" data-order="${nextValue}" data-bs-toggle="dropdown" aria-expanded="false">Select Task</button>`)
        nextValue = nextValue + 1;
        html.push('<ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">')

        for(task of allTasks) {
            html.push(`<li class="dropdown-item" data-taskId="${task.id}">${task.taskName}</li>`);
        }
        html.push('</ul>');
        html.push(`<button type="button" class="close" aria-label="Close"><span aria-hidden="true">&times;</span></button>`);
        html.push('</div>');

        $(this).parent().before(html.join(""));
     });

     $("#createWorkflowButton").click(function (event) {
       event.preventDefault();
           var taskOrderList = [];
           $('.selectedValue').each(function() {
              taskId = $(this).attr("data-taskId");
              order = $(this).attr("data-order");
              if(taskId != '') {
                taskOrderList.push({taskId: taskId, order: order});
              }
              console.log(taskOrderList);
           });
           data = {
                name: $('#workflowName').val(),
                taskOrderList: taskOrderList
           }
           $.ajax({
               type: "POST",
               url: '/workflowSpec/create',
               data: JSON.stringify(data),
               dataType: "json",
               contentType : 'application/json',
               encode: true,
               success: function (response) {
                   var msg = '<span class="alert alert-primary" role="alert" style="padding: 5px;">'
                               +'Workflow Spec Created!'
                           +'</span>';

                   $('#createWorkflowSpec').find('#message').html(msg);
                   $('#createWorkflowSpec').find('#message').removeClass('hide');
               },
               error: function (x, e) {
                   console.log(e);
               }
           });
       });

       $(document).on('click', '.close', function() {
           selectedValueElement = $(this).siblings(".selectedValue");
           removedTaskOrder = selectedValueElement.attr("data-order");
           $(this).parent().remove();
           $('.selectedValue').each(function() {
             order = $(this).attr("data-order");
             if(order > removedTaskOrder) {
                $(this).attr("data-order", $(this).attr("data-order") - 1);
             }
          });
          nextValue -= 1;
       });
});

function deployWorkflow(obj){
    var workflowAccItem = $(obj).parents('div.accordion-item');
    var workflowSpecId = $(workflowAccItem).find('#workflowSpecId').text();
    var deploymentStatus = $(workflowAccItem).find('#workflowSpecDeploymentStatus').text();

    data = {
        'workflowSpecId': workflowSpecId
    };


    if(workflowSpecId != null && workflowSpecId.length > 0){
        if(deploymentStatus !== null && deploymentStatus === 'Deployed'){
            // alert('WorkflowSpec is already deployed!');
            return;
        }

        $.ajax({
           type: "GET",
           url: '/'+workflowSpecId+'/deploy',
           data: JSON.stringify(data),
           dataType: "json",
           contentType : 'application/json',
           encode: true,
           success: function (response) {
               // alert('Workflow Spec is deployed');

               var message =
                    '<div class="alert alert-primary" role="alert">'
                        +'Workflow Specification Deployed!'
                    +'</div>';

               $(workflowAccItem).find('#message').html(message);

               setTimeout(() => {
                    $('.navbar-nav').find('a.active').trigger('click');
               }, 2000);
           },
           error: function(err) {
               console.log('some error occurred inside deployWorkflow() :::: createWorkflow.js, error = '+err);
           }
        });
    }
}