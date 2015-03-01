

// This #include statement was automatically added by the Spark IDE.
#include "HttpClient/HttpClient.h"

#include <string.h>
#define ID "53ff6b066678505521351367"

HttpClient http;

// Define the pins we're going to call pinMode on
int led = D7;  // You'll need to wire an LED to this one to see it blink.
int state = HIGH;
http_request_t request;
http_response_t response;
http_header_t headers[] = {{"Content-Type", "application/x-www-form-urlencoded"}, {NULL, NULL}};
int turnOff(String args);
int turnOn(String args);
int toggle(String args);

// This routine runs only once upon reset
void setup() {
  // Initialize D0 + D7 pin as output
  // It's important you do this here, inside the setup() function rather than outside it or in the loop function.
  //Serial.begin(9600);
  pinMode(led, OUTPUT);
  pinMode(D1, OUTPUT);
  pinMode(D0, INPUT);
  digitalWrite(led, HIGH);
  digitalWrite(D1, HIGH);
  Spark.variable("state", &state, INT);
  Spark.function("Off", turnOff);
  Spark.function("On", turnOn);
  Spark.function("Toggle", toggle);
  request.hostname = "buttonlight.herokuapp.com";
  request.port = 80;
  request.path = "/text";
  request.body = "";
  http.post(request,response,headers);
  delay(1000);
  //client = new HttpClient("http://buttonlight.herokuapp.com", 80);// Turn ON the LED pins
}

// This routine gets called repeatedly, like once every 5-15 milliseconds.
// Spark firmware interleaves background CPU activity associated with WiFi + Cloud activity with your code. 
// Make sure none of your code delays or blocks for too long (like more than 5 seconds), or weird things can happen.

int counter =0;
int resetCounter = 0;
int textCounter = 0;

void loop() {
    int buttonState = digitalRead(D0);
  
    if(buttonState==LOW)
    {
   
        counter++;
        if(counter==100)
        {
            //counter =0;
            state=!state;
            digitalWrite(led, state); 
            request.path = "/status/";
            request.body = "status="+String(state)+"&device_id="+String(ID);
            http.post(request,response,headers);
            //Serial.println(response.body);
            /**
             client.connect("http://buttonlight.herokuapp.com", 80);
            client.println("POST /status/ HTTP/1.1");
            client.println("Host: buttonlight.herokuapp.com");
            client.println("Content-Length: 8");
            client.println("Content-Type: application/x-www-form-urlencoded");
            client.println();
            client.print("status=");
            client.print(state);
            client.println();
            client.println();
            **/
        }

    }
    else
    {
        counter = 0;
    }
               // Wait for 1 second in off mode
    if (state==LOW)
    {
        resetCounter++;
        textCounter=0;
        if(resetCounter == 1000)
        {
            state=HIGH;
            digitalWrite(led, state); 
            request.path = "/status/";
            request.body = "status="+String(state)+"&device_id="+String(ID);
            http.post(request,response,headers);
            
        
        }
    }
    else
    {
        resetCounter=0;
        textCounter++; 
        if(textCounter == 2000)
        {
        
            digitalWrite(led, LOW); 
            delay(100);
            digitalWrite(led, HIGH); 
            request.path = "/text";
            request.body = "";
            http.post(request,response,headers);
            
            
            
        
        }
        
    }
}


int turnOff(String args)
{
    
    state=LOW;
    digitalWrite(led, state); 
    request.path = "/status/";
    request.body = "status="+String(state)+"&device_id="+String(ID);
    http.post(request,response,headers);
    return state;
    
}


int turnOn(String args)
{
    
    state=HIGH;
    digitalWrite(led, state); 
    request.path = "/status/";
    request.body = "status="+String(state)+"&device_id="+String(ID);
    http.post(request,response,headers);
    return state;
    
}


int toggle(String args)
{
    
    state=!state;
    digitalWrite(led, state); 
    request.path = "/status/";
    request.body = "status="+String(state)+"&device_id="+String(ID);
    http.post(request,response,headers);
    return state;
    
}
