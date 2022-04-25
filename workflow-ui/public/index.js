$(function () {
  const statusColorMap = new Map();
  statusColorMap.set('Completed', 'complete');
  statusColorMap.set('In_Progress', 'in-progress');
  statusColorMap.set('Not_Started', 'not-started');
  const statusCodeMap = new Map();
  statusCodeMap.set('Completed', 'Completed');
  statusCodeMap.set('In_Progress', 'In Progress');
  statusCodeMap.set('Not_Started', 'Not Started');
  $(document.body).tooltip({
      selector: "[title]",
      //trigger: "click",
      placement: "auto bottom",
      html: "true"
  });
  function generateWF() {
      $("#workflow-diagram").empty();
      $("#workflow-status").empty();
      $('#loader').show();
      const workflowId = $("#workflowId").attr("data-workflowId");
      url = `/workflowStatus/${workflowId}`;
      $.ajax({
          url: url,
          type: 'GET',
          success: function (response) {
              $("#workflow-status").append(`<h3>${response.name}</h3>`);
              $("#workflow-status").append(`<h4>${statusCodeMap.get(response.status)}</h4>`);
              let k = response.taskList.length - 1;
              var html = [];
              response.taskList.sort((a, b) => a.order - b.order);
              for (task of response.taskList) {
                  html.push('<div class="task">');
                  const task_status_color = statusColorMap.get(task.status);
                  const title = `Task: ${task.task_name}<br/>Status: ${statusCodeMap.get(task.status)}`;
                  html.push(`<span class="task-circle color-${task_status_color}" data-toggle="tooltip" title="${title}"></span>`);
                  html.push(`<span class="task-name">${task.task_name}</span>`);
                  html.push('</div>');
                  if (k > 0) {
                      html.push(`<hr class="color-${task_status_color}"/>`);
                  }
                  k--;
              }
              $("#workflow-diagram").append(html.join(""));
              $('#loader').hide();
              
          },
          error: function (x, e) {
              console.log(e);
          }
      });
  }
  generateWF();
  //setInterval(generateWF(), 30000);
});