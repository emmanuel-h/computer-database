<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
<title><spring:message code="appName"/></title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/font-awesome.css" rel="stylesheet" media="screen">
<link href="css/main.css" rel="stylesheet" media="screen">
</head>
<body>
    <header class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
            <a class="navbar-brand" href="dashboard"><spring:message code="header.title"/></a>
        </div>
    </header>
    <section id="main">
        <div class="container">
            <c:if test="${not empty message}">
                <div class="alert alert-warning">${message}</div>
            </c:if>
            <div class="row">
                <div class="col-xs-8 col-xs-offset-2 box">
                    <div class="label label-default pull-right">
                        id: ${computer.id}
                    </div>
                    <h1><spring:message code="editComputer.title"/></h1>

                    <form:form action="editComputerAction" modelAttribute="computer" method="POST">
                        <form:input type="hidden" value="${computer.id}" name="id" path="id"/>
                        <fieldset>
                            <div class="form-group">
                                <label for="computerName"><spring:message code="editComputer.computerName"/></label>
                                <form:input type="text" class="form-control" name="computerName"
                                placeholder="Computer name" value="${computer.name}" path="name"/>
                            </div>
                            <div class="form-group">
                                <label for="introduced"><spring:message code="editComputer.introduced"/></label>
                                <form:input type="date" class="form-control" name="introduced"
                                placeholder="Introduced date" value="${computer.introduced}" path="introduced"/>
                            </div>
                            <div class="form-group">
                                <label for="discontinued"><spring:message code="editComputer.discontinued"/></label>
                                <form:input type="date" class="form-control" name="discontinued"
                                placeholder="Discontinued date" value="${computer.discontinued}" path="discontinued"/>
                            </div>
                            <div class="form-group">
                                <label for="companyId"><spring:message code="editComputer.company"/></label>
                                <form:select class="form-control" name="companyId" path="manufacturer">
                                    <option value="0">--</option>
                                    <c:forEach items="${companies}" var="company">
                                        <c:if test="${company.id == companyId}">
                                            <option selected value="${company.id}">${company.name}</option>
                                        </c:if>
                                        <c:if test="${company.id != companyId}">
                                            <option value="${company.id}">${company.name}</option>
                                        </c:if>
                                    </c:forEach>
                                </form:select>
                            </div>            
                        </fieldset>
                        <div class="actions pull-right">
                            <input type="submit" value="<spring:message code='editComputer.editButton'/>" class="btn btn-primary">
                            <spring:message code="editComputer.or"/>
                            <a href="dashboard" class="btn btn-default"><spring:message code="editComputer.cancelButton"/></a>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </section>
</body>
</html>