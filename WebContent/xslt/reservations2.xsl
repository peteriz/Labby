<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
			<xsl:for-each select="//RESERVATION[not(GROUP=preceding-sibling::RESERVATION/GROUP)]/GROUP">
				<xsl:variable name="groupid">
				<xsl:value-of select="." />
			</xsl:variable>
			<table class="tablesorter">
			<caption><xsl:value-of select="//RESERVATION[GROUP=$groupid]/GROUP" /> group</caption>
			<thead class="center">
				<tr>
					<th>Instrument ID</th>
					<th>Instrument Type</th>
					<th>Timeslots</th>
				</tr>
			</thead>
			<tbody class="center">
				<xsl:for-each select="//RESERVATION[GROUP=$groupid]">
				<tr>
					<td><xsl:value-of select="INSTRUMENTID" /></td>
					<td><xsl:value-of select="INSTRUMENTTYPE" /></td>
					<td><xsl:value-of select="TIMESLOTS" /></td>
				</tr>
				</xsl:for-each>
			</tbody>
			</table>
			<br/>
			</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
