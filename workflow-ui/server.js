'use strict';

const express = require('express');
const request = require('request');

// Constants
const PORT = 8080;
const HOST = '0.0.0.0';

// App

const app = express();
app.use(express.static('public'))
app.set('view engine', 'ejs');

app.get('/workflow/:workflowId', (req, res) => {
       
    res.render('workflow', {
        workflowId: req.params['workflowId']
    });
  
});

app.get('/workflowStatus/:workflowId', (req, res) => {
    let workflowId = req.params['workflowId'];
    res.contentType("application/json");
    request.get(`https://d566b659-1d19-4148-a4f8-09fb2e4370cd.mock.pstmn.io/getWorkflowStatus/${workflowId}`, {timeout: 30000}, function (error, response, body) {
        console.error('error:', error); // Print the error if one occurred
        console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
        console.log('body:', body);
        if(error != null) {
            const errorJson = {
                error: error
            }
            res.send(JSON.stringify(errorJson));
        }
        else if(response.statusCode === 200) {
            res.send(body);
        }
        else {
            const errorJson = {
                response: response
            }
            res.send(JSON.stringify(errorJson));
        }
    });
  
});

app.listen(PORT, HOST);
console.log(`Running on http://${HOST}:${PORT}`);