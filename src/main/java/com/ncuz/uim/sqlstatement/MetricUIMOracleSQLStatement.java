package com.ncuz.uim.sqlstatement;

public class MetricUIMOracleSQLStatement {
    public static String findQOS_Data="  select   " +
            "qos, source, target,origin, table_id, probe, sampletime, samplevalue, samplerate from s_qos_data where   " +
            " $Source $QOS "+
            " group by qos, source, target,origin, table_id, probe, sampletime, samplevalue,samplerate ";

    public static String findQOSAll="select name,R_TABLE,H_TABLE,D_TABLE,V_TABLE from s_qos_definition GROUP BY name,R_TABLE,H_TABLE,D_TABLE,V_TABLE";

    public static String findQOS="select  b.QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE $target from s_qos_definition a " +
            "inner join s_qos_data b on a.NAME=b.QOS ";
    public static String findQOSBySource="select  b.QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE from s_qos_definition a " +
            "inner join s_qos_data b on a.NAME=b.QOS where SOURCE=? group by QOS";
    public static String findTargetBySource="select  b.qos,a.R_TABLE,a.H_TABLE,a.D_TABLE,b.TARGET from s_qos_definition a " +
            "inner join s_qos_data b on a.NAME=b.QOS where SOURCE=? and QOS=?";
    public static String findQOSByName="select  b.qos,a.R_TABLE,a.H_TABLE,a.D_TABLE,b.TARGET from s_qos_definition a " +
            "inner join s_qos_data b on a.NAME=b.QOS  where name=$name";
    public static String findMetricByParameters2="select d.origin, d.table_id, d.source, d.target,"+
    "d.probe, r.sampletime, r.samplevalue, r.samplerate "+
    "from s_qos_data d join $RTable as r on d.table_id = r.table_id";

    private String findMetricByPeriodLatest="";

    public static String findMetricByParameters=
            "select * from ( "+
                    "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, 300 as samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                where "+
                    "                source in ( $Source ) "+
                    "                and target in ( $Target ) "+
                    "                 UNION "+
                    "  select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     where "+
                    "      source in ( $Source ) "+
                    "       and target in ( $Target ) "+
                    "        UNION "+
                    "         select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            where "+
                    "             source in ( $Source ) "+
                    "              and target in ( $Target ) "+
                    "       ) QOS "+
                    " where QOS.sampletime >= TO_TIMESTAMP($startPeriod,'DD-MON-YY HH24.MI.SS')  and " +
                    " QOS.sampletime <= TO_TIMESTAMP($endPeriod,'DD-MON-YY HH24.MI.SS')   " +
                    "order by source, target ,QOS.sampletime desc";

    public static String findMetricByParametersLatest=
            "select main.*, "+
                    " (select samplevalue from $RTable where main.table_id=table_id and main.sampletime=sampletime "+
                    " union "+
                    " select sampleavg as samplevalue from $HTable where main.table_id=table_id and main.sampletime=sampletime "+
                    " union "+
                    " select sampleavg as samplevalue from $DTable where main.table_id=table_id and main.sampletime=sampletime)"+
            " as samplevalue  from ("+
            "select qos,origin,table_id, source, target,probe, max(sampletime) as sampletime,  samplerate from ( "+
                    "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, 300 as samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                where "+
                    "                source in ( $Source ) "+
                    "                and target in ( $Target ) "+
                    "                 UNION "+
                    "  select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     where "+
                    "      source in ( $Source ) "+
                    "       and target in ( $Target ) "+
                    "        UNION "+
                    "         select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            where "+
                    "             source in ( $Source ) "+
                    "              and target in ( $Target ) "+
                    "       ) QOS "+
                    "GROUP BY  qos,origin,table_id, source, target,probe,   samplerate ) main  ";

    public static String findMetricByParametersPlain=
//            "select * from ( " +
            "select * from ( "+
                    "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, 300 as samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                $where "+
                    "               $Source "+
                    "              $Target "+
                    "                 UNION "+
                    "  select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     $where "+
                    "      $Source "+
                    "              $Target "+
                    "        UNION "+
                    "         select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            $where "+
                    "             $Source "+
                    "              $Target "+
                    "       ) QOS "+
                    " where QOS.sampletime >= TO_TIMESTAMP($startPeriod,'DD-MON-YY HH24.MI.SS')  and " +
                    " QOS.sampletime <= TO_TIMESTAMP($endPeriod,'DD-MON-YY HH24.MI.SS')   " +
//                    "order by source, target ,QOS.sampletime asc"
                    " order by source, target ,QOS.sampletime desc"
//            +") ASD where target='dashboardpapp10' "
            ;

    public static String findMetricByParametersLatestPlain=
            "select main.*, "+
                    " (select samplevalue from $RTable where main.table_id=table_id and main.sampletime=sampletime "+
                    " union "+
                    " select sampleavg as samplevalue from $HTable where main.table_id=table_id and main.sampletime=sampletime "+
                    " union "+
                    " select sampleavg as samplevalue from $DTable where main.table_id=table_id and main.sampletime=sampletime)"+
                    " as samplevalue  from ("+
                    "select qos,origin,table_id, source, target,probe, max(sampletime) as sampletime,  samplerate from ( "+
                    "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, 300 as samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                $where "+
                    "                $Source "+
                    "              $Target "+
                    "                 UNION "+
                    "  select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     $where "+
                    "      $Source "+
                    "              $Target "+
                    "        UNION "+
                    "         select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            $where "+
                    "             $Source "+
                    "              $Target "+
                    "       ) QOS "+
                    "GROUP BY  qos,origin,table_id, source, target,probe,   samplerate ) main  ";
}
