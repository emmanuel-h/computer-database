<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
					<h1><spring:message code="addComputer.title"/></h1>
					<form action="createComputer" method="POST" id="addComputerForm">
						<fieldset>
							<div class="form-group">
								<label for="computerName"><spring:message code="addComputer.name"/></label> <input
									type="text" class="form-control" name="name" id="name"
									placeholder="Computer name" required>
									<label id="nameProblem" hidden=true class="error text-danger">This field is required</label>
							</div>
							<div class="form-group">
								<label for="introduced"><spring:message code="addComputer.introduced"/></label> <input
									type="date" class="form-control" name="introduced"
									placeholder="Introduced date">
							</div>
							<div class="form-group">
								<label for="discontinued"><spring:message code="addComputer.discontinued"/></label> <input
									type="date" class="form-control" name="discontinued"
									placeholder="Discontinued date">
							</div>
							<div class="form-group">
								<label for="companyId"><spring:message code="addComputer.company"/></label> <select
									class="form-control" name="company">
									<option value="0">--</option>
									<c:forEach items="${companies}" var="company">
										<option value="${company.id}">${company.name}</option>
									</c:forEach>
								</select>
							</div>
						</fieldset>
						<div class="actions pull-right">
							<input type="submit" value="<spring:message code='addComputer.addButton'/>"
								class="btn btn-primary"> <spring:message code="addComputer.or"/> <a href="dashboard"
								class="btn btn-default"><spring:message code="addComputer.cancelButton"/></a>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>
	
    <script src="js/jquery.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
    <script src="js/addComputer.js"></script>
</body>
</html>