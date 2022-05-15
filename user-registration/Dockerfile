# syntax=docker/dockerfile:1
FROM python:3.9.7-slim

WORKDIR /app

# add the current directory to the container as /app
ADD . /app

RUN pip install --trusted-host pypi.python.org -r requirements.txt

#ENV FLASK_APP=app.py

EXPOSE 80

# execute the Flask app
CMD ["python", "app.py"]
