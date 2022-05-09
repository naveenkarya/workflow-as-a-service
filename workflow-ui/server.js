'use strict';

const express = require('express');
const request = require('request');
const bodyParser = require('body-parser')


// Constants
const PORT = 8080;
const HOST = '0.0.0.0';
const schedulerServiceUrl = "https://3bd9eb30-a041-4fa7-90c7-9f2b6f92a960.mock.pstmn.io";
const backendServiceUrl = "https://3bd9eb30-a041-4fa7-90c7-9f2b6f92a960.mock.pstmn.io";
// App

const app = express();
app.use(express.static('public'))
app.set('view engine', 'ejs');
app.use(bodyParser.json());

app.get('/workflow/:workflowId', (req, res) => {
    res.render('workflow', {
        workflowId: req.params['workflowId']
    });
});

app.get('/dashboard', (req, res) => {
    res.render('dashboard', {});
});

app.get('/', (req, res) => {
    res.render('dashboard', {});
});

app.get('/workflowList', (req, res) => {
    res.contentType("application/json");
    request.get(`${schedulerServiceUrl}/workflowList`, { timeout: 30000 }, function (error, response, body) {
        console.error('error:', error); // Print the error if one occurred
        console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
        console.log('body:', body);
        if (error != null) {
            const errorJson = {
                error: error
            }
            res.send(JSON.stringify(errorJson));
        }
        else if (response.statusCode === 200) {
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

app.get('/workflowSpec', (req, res) => {
    res.contentType("application/json");
    request.get(`${backendServiceUrl}/workflowSpec`, { timeout: 30000 }, function (error, response, body) {
        console.error('error:', error); // Print the error if one occurred
        console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
        console.log('body:', body);
        if (error != null) {
            const errorJson = {
                error: error
            }
            res.send(JSON.stringify(errorJson));
        }
        else if (response.statusCode === 200) {
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

app.post('/createWorkflow', (req, res) => {
    console.log(req.body);
    res.contentType("application/json");
    request.post({
        url: `${schedulerServiceUrl}/createWorkflow`,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(req.body),
        timeout: 30000
    }, function (error, response, body) {
        console.error('error:', error); // Print the error if one occurred
        console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
        console.log('body:', body);
        if (error != null) {
            const errorJson = {
                error: error
            }
            res.send(JSON.stringify(errorJson));
        }
        else if (response.statusCode === 200) {
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

app.get('/workflowStatus/:workflowId', (req, res) => {
    let workflowId = req.params['workflowId'];
    res.contentType("application/json");
    request.get(`${schedulerServiceUrl}/getWorkflowStatus/${workflowId}`, { timeout: 30000 }, function (error, response, body) {
        console.error('error:', error); // Print the error if one occurred
        console.log('statusCode:', response && response.statusCode); // Print the response status code if a response was received
        console.log('body:', body);
        if (error != null) {
            const errorJson = {
                error: error
            }
            res.send(JSON.stringify(errorJson));
        }
        else if (response.statusCode === 200) {
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