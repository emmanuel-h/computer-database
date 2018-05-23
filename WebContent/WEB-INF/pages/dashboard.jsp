<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="mylib" uri="/WEB-INF/taglibs/mylib.tld"%>
<!DOCTYPE html>
<html>
<head>
<title>Computer Database</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/font-awesome.css" rel="stylesheet" media="screen">
<link href="css/main.css" rel="stylesheet" media="screen">
</head>
<body>
	<header class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<a class="navbar-brand" href="dashboard"> Application - Computer
				Database </a>
		</div>
	</header>

	<section id="main">
		<div class="container">
            <c:if test="${not empty message}">
                <div class="alert alert-warning">${message}</div>
            </c:if>
			<h1 id="homeTitle">${nbComputers} Computers found</h1>
			<div id="actions" class="form-horizontal">
				<div class="pull-left">
					<form id="searchForm" action="dashboard" method="GET" class="form-inline">
						<input type="search" id="searchbox" name="search"
							class="form-control" placeholder="Search name" />
						 <input type="submit" id="searchsubmit" value="Filter by name"
							class="btn btn-primary" />
					</form>
				</div>
				<div class="pull-right">
					<a class="btn btn-success" id="addComputer" href="addComputer">Add
						Computer</a> <a class="btn btn-default" id="editComputer" href="#"
						onclick="$.fn.toggleEditMode();">Edit</a>
				</div>
			</div>
		</div>

		<form id="deleteForm" action="deleteComputers" method="POST">
			<input type="hidden" name="selection" value="">
		</form>

		<div class="container" style="margin-top: 10px;">
			<table class="table table-striped table-bordered">
				<thead>
					<tr>
						<!-- Variable declarations for passing labels as parameters -->
						<!-- Table header for Computer Name -->

						<th class="editMode" style="width: 60px; height: 22px;"><input
							type="checkbox" id="selectall" /> <span
							style="vertical-align: top;"> - <a href="#"
								id="deleteSelected" onclick="$.fn.deleteSelected();"> <i
									class="fa fa-trash-o fa-lg"></i>
							</a>
						</span></th>
						<th>Computer name</th>
						<th>Introduced date</th>
						<!-- Table header for Discontinued Date -->
						<th>Discontinued date</th>
						<!-- Table header for Company -->
						<th>Company</th>

					</tr>
				</thead>
				<!-- Browse attribute computers -->
				<tbody id="results">
					<c:forEach var="computer" items="${computerList}">
						<tr>
							<td class="editMode"><input type="checkbox" name="cb"
								class="cb" value="${computer.id}"></td>
							<td><a href="editComputer?id=${computer.id}">${computer.name}</a>
							</td>
							<td>${computer.introduced}</td>
							<td>${computer.discontinued}</td>
							<td>${computer.manufacturer}</td>

						</tr>
					</c:forEach>


				</tbody>
			</table>
		</div>
	</section>

	<footer class="navbar-fixed-bottom">
		<div class="container text-center">
			<mylib:pagination uri="dashboard"
				page="${page}" totalPages="${maxPage}"
				results="${results}" search="${search}"/>

			<div class="btn-group btn-group-sm pull-right" role="group">
            <c:if test="${not empty search}">
                <a class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="10" search="${search}"/>>10</a> <a
                    class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="50" search="${search}"/>>50</a> <a
                    class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="100" search="${search}"/>>100</a>
            </c:if>
            <c:if test="${empty search}">
                <a class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="10"/>>10</a> <a
                    class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="50"/>>50</a> <a
                    class="btn btn-default"
                    href=<mylib:link target="dashboard" page="${page}" limit="100"/>>100</a>
            </c:if>
			</div>
		</div>
	</footer>
	<script src="js/jquery.min.js"></script>
	<script src="js/bootstrap.min.js"></script>
	<script src="js/dashboard.js"></script>

</body>
</html>