package no.hiof.trace.db.values;

public class Columns 
{
	public static final String[] PLAN_COLUMNS = {ColumnName.ID, ColumnName.NAME, ColumnName.DESCRIPTION, ColumnName.SSID, ColumnName.NFC, ColumnName.LONG, ColumnName.LAT, ColumnName.AUTO_REG, ColumnName.STATUS, ColumnName.PRIMARY_TASK};
	public static final String[] TASK_COLUMNS = {ColumnName.ID, ColumnName.NAME, ColumnName.DESCRIPTION, ColumnName.PLAN_ID, ColumnName.STATUS};
	public static final String[] INTERVAL_COLUMNS = {ColumnName.ID, ColumnName.START_TIME, ColumnName.END_TIME, ColumnName.TASK_ID};
	public static final String[] TASK_STATUS_COLUMNS = {ColumnName.STATUS};
	public static final String[] PLAN_STATUS_COLUMNS = {ColumnName.STATUS};
}
