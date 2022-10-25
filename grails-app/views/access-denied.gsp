<g:applyLayout name="page">
<html>
	<head>
	</head>

	<body>
		<content tag="application">
		<bootstrap:div style="text-align: center; padding-top: 10%">
			<h3>
				<strong> ${message(code: 'app.accessDenied.label', default: 'Access denied')}
				</strong>
			</h3>
			<br />

			<h1>
				${message(code: 'app.accessDeniedMessage.label', default: 'Forbidden: You don\'t have permission to access requested page')}
			</h1>
		</bootstrap:div>
		</content>
	</body>
</html>
</g:applyLayout>
