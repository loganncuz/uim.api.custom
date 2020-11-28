package com.ncuz.uim.sqlstatement;

public class MetricUIMSQLStatement {
    public static String findQOS_Data="  select   " +
            "qos, source, target,origin, table_id, probe, sampletime, samplevalue, samplerate from s_qos_data where   " +
            " $Source $QOS "+
            " group by qos, source, target,origin, table_id, probe, sampletime, samplevalue,samplerate ";

    public static String findQOSAll="select name,R_TABLE,H_TABLE,D_TABLE,V_TABLE from s_qos_definition GROUP BY name,R_TABLE,H_TABLE,D_TABLE,V_TABLE";

    public static String findQOS="select  b.QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE,b.TARGET from s_qos_definition a " +
            "inner join s_qos_data b on a.`NAME`=b.QOS ";
    public static String findQOSBySource="select  b.QOS,a.R_TABLE,a.H_TABLE,a.D_TABLE from s_qos_definition a " +
            "inner join s_qos_data b on a.`NAME`=b.QOS where SOURCE=$name group by QOS";
    public static String findTargetBySource="select  b.qos,a.R_TABLE,a.H_TABLE,a.D_TABLE,b.TARGET from s_qos_definition a " +
            "inner join s_qos_data b on a.`NAME`=b.QOS where SOURCE=$source and name=$name";
    public static String findQOSByName="select  b.qos,a.R_TABLE,a.H_TABLE,a.D_TABLE,b.TARGET from s_qos_definition a " +
            "inner join s_qos_data b on a.`NAME`=b.QOS  where name=$name group by b.qos";
    public static String findMetricByParameters2="select d.origin, d.table_id, d.source, d.target,"+
    "d.probe, r.sampletime, r.samplevalue, r.samplerate "+
    "from s_qos_data d join $RTable as r on d.table_id = r.table_id";

    public static String findMetricByParameters=
            "select * from ( "+
            "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
    "                d.probe, r.sampletime, r.samplevalue, r.samplerate "+
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
     " where STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')>= STR_TO_DATE($startPeriod, '%d-%b-%y %H.%i.%s.%f') and " +
     " STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')>= STR_TO_DATE($endPeriod, '%d-%b-%y %H.%i.%s.%f') " +
     "order by source, target ,STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f') desc";


    public static String findMetricByParametersLatest=
            "select qos,origin,table_id, source, target,probe,DATE_FORMAT(max(STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')),'%d-%b-%y %H.%i.%s.%f') sampletime, samplevalue, samplerate from ( "+
                    "        select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, r.samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                where "+
                    "                source in ( $Source ) "+
                    "                and target in ( $Target ) "+
                    "                 UNION "+
                    "  select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     where "+
                    "      source in ( $Source ) "+
                    "       and target in ( $Target ) "+
                    "        UNION "+
                    "         select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            where "+
                    "             source in ( $Source ) "+
                    "              and target in ( $Target ) "+
                    "       ) QOS "+
                    "GROUP BY origin,source,target  ";

    public static String findMetricByParametersPlain=
            "select * from ( "+
                    "        select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, r.samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                $where "+
                    "                $Source "+
                    "                $Target "+
                    "                 UNION "+
                    "  select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     $where "+
                    "      $Source "+
                    "       $Target "+
                    "        UNION "+
                    "         select d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            $where "+
                    "             $Source "+
                    "              $Target "+
                    "       ) QOS "+
                    " where STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')>= STR_TO_DATE($startPeriod, '%d-%b-%y %H.%i.%s.%f') and " +
                    " STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')>= STR_TO_DATE($endPeriod, '%d-%b-%y %H.%i.%s.%f') " +
                    "order by source, target ,STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f') desc";


    public static String findMetricByParametersLatestPlain=
            "select qos,origin,table_id, source, target,probe,DATE_FORMAT(max(STR_TO_DATE(QOS.sampletime, '%d-%b-%y %H.%i.%s.%f')),'%d-%b-%y %H.%i.%s.%f') sampletime, samplevalue, samplerate from ( "+
                    "        select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "                d.probe, r.sampletime, r.samplevalue, r.samplerate "+
                    "                from s_qos_data d join $RTable r on d.table_id = r.table_id "+
                    "                $where "+
                    "                $Source "+
                    "                $Target "+
                    "                 UNION "+
                    "  select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "   d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "    from s_qos_data d join $HTable r on d.table_id = r.table_id "+
                    "     $where "+
                    "      $Source "+
                    "      $Target "+
                    "        UNION "+
                    "         select  d.qos,d.origin, d.table_id, d.source, d.target, "+
                    "          d.probe, r.sampletime, r.sampleavg as samplevalue, 300 as samplerate "+
                    "           from s_qos_data d join $DTable r on d.table_id = r.table_id "+
                    "            $where "+
                    "            $Source "+
                    "              $Target "+
                    "       ) QOS "+
                    "GROUP BY origin,source,target  ";
}
