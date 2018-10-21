<%-- 
    Document   : index
    Created on : Feb 22, 2016, 10:50:57 AM
--%>

<%@page import="EventParser.JSONParser"%>
<%@page import="constants.Constants"%>
<%@page import="commons.Initializer"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    HashMap<String, Integer> areaSupport = JSONParser.main(null);
    System.out.println(areaSupport);
    if(areaSupport == null || areaSupport.isEmpty()) {
        areaSupport = new HashMap<String, Integer>();
        System.err.println("No candidates with high support.");
    }
%>
<%!
    public void init() {
        System.out.println("Initializing...");
    }
%>


<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="js/canvasjs.min.js"></script>
        <script type="text/javascript">
        window.onload = function () {
                var chart = new CanvasJS.Chart("chartContainer", {
                        
                        theme: "theme1",//theme1
                        title:{
                            
                                text: "Vixlet Applications Activity Monitor wrt Time"
                                
                        },
                      
                        animationEnabled: true,
                        animationDuration: 2000,// change to true
                        axisX:{
                            labelFontSize: 15,
                            labelAngle: 90,
        title: "Most Popular event at -Time",
        titleFontSize: 25
        
      },
      axisY:{
          labelFontSize: 15,
        title: "No. of events recorded",
        titleFontSize: 25
      },
                        data: [              
                        {
                                // Change type to "bar", "area", "spline", "pie", "column",etc.
                                
                    type: "area",
                                    
                    dataPoints: [
                                    <%
                                        int i=0;
                                        for(Map.Entry<String, Integer> entry : areaSupport.entrySet()) {
                                            if(i != 0) {
                                                %>,<%
                                            }
                                    %>
                                        { label: "<%=entry.getKey() %>",  y: <%=entry.getValue() %>  }
                                    <%
                                            i++;
                                        }
                                    %>
                                ]
                        }
                        ]
                        
                });
                chart.render();
        }
        </script>
        <title>Apriori All</title>
    </head>
    <body>
        <div id="chartContainer" style="height: 700px; width: 100%;"></div>
    </body>
</html>