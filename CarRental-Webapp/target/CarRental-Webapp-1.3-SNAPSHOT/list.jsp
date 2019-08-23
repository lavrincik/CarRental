<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>CarRental</title>
    <link href="${pageContext.request.contextPath}/style.css" rel="stylesheet" type="text/css" >
</head>

<body>
<header>
    <h1>Car-Rental</h1>
</header>

<div class="import">
    <h2>Zadejte auto</h2>
    <form action="${pageContext.request.contextPath}/CarRental/addCar" method="post">
        <table>
            <tr>
                <th>Car brand:</th>
                <td><input type="text" name="carBrand" value="<c:out value='${param.carBrand}'/>"/></td>
            </tr>
            <tr>
                <th>Description:</th>
                <td><input type="text" name="description" value="<c:out value='${param.description}'/>"/></td>
            </tr>
            <tr>
                <th>CZK/day:</th>
                <td><input type="text" name="dailyPrice" value="<c:out value='${param.dailyPrice}'/>"/></td>
            </tr>
        </table>
        <input type="Submit" value="Zadat" />
    </form>
</div>
<div class="tabulka">
    <table border="1">
        <thead>
        <tr>
            <th width="200">Car brand</th>
            <th width="200">Description</th>
            <th width="200">CZK/day</th>
        </tr>
        </thead>
        <c:forEach items="${cars}" var="car">
            <tr>
                <td><c:out value="${car.carBrand}"/></td>
                <td><c:out value="${car.description}"/></td>
                <td><c:out value="${car.dailyPrice}"/></td>
                <td><form method="post" action="${pageContext.request.contextPath}/CarRental/deleteCar?carId=${car.id}"
                          style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
            </tr>
        </c:forEach>
    </table>
</div>
<div style="clear: both">
</div>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: #0d1143; padding: 10px; border-radius: 5px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
</body>
</html>