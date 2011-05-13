<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
			<xsl:for-each select="//RESERVATION[not(INSTRUMENTID=preceding-sibling::RESERVATION/INSTRUMENTID)]/INSTRUMENTID">
				<xsl:variable name="instid">
				<xsl:value-of select="." />
			</xsl:variable>
			<table class="tablesorter">
			<caption>Instrument <xsl:value-of select="//RESERVATION[INSTRUMENTID=$instid]/INSTRUMENTTYPE" /> With ID <xsl:value-of select="$instid" /></caption>
			<thead class="center">
				<tr>
					<th>Group</th>
					<th>Timeslots</th>
				</tr>
			</thead>
			<tbody class="center">
				<xsl:for-each select="//RESERVATION[INSTRUMENTID=$instid]">
				<tr >
					<td><xsl:value-of select="GROUP" /></td>
					<td><xsl:value-of select="TIMESLOTS" /></td>
				</tr>
				</xsl:for-each>
			</tbody>
			</table>
			<br/>
			</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
