<%--

  Weather component.

  Display weather data 

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@ page contentType="text/css" %>
<html>
    <head>
    <link href="/apps/AEM-Exercise/components/content/weather/clientlib/weather.css" rel="stylesheet" type="text/css">
    </head>
<div class="temp minTemp"><span> Maximum temperature: ${properties.val}F</span></div>







