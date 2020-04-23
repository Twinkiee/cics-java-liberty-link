package com.sample.sp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.commarea.CT10025OCommareaWrapper1;
import com.sample.commarea.CT10025XCommareaWrapper1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ctj10025SpProgram01 {

  private static final Logger logger = LoggerFactory.getLogger(Ctj10025SpProgram01.class);

  public void execute(
      String[] returnCode
      , int[] sqlCode
      , String[] errorDesc
      , byte[][] inout) {
    logger.info("Executing [ CTJ10025 ] SP program");

    long time = System.nanoTime();

    byte[] input = getInput(inout);

    try (Connection connection = getConnection()) {

      connection.setAutoCommit(false);

      CT10025XCommareaWrapper1 wrapper = new CT10025XCommareaWrapper1(input);

      final CT10025OCommareaWrapper1 returnValue = doSelect(connection, wrapper);

      returnValue(returnValue, returnCode, sqlCode, errorDesc, inout);

      logCommarea(returnValue);
      logger.info("[ CTJ10025 ] SP program executed in [ {} ] ns", System.nanoTime() - time);
    } catch (SQLException | JsonProcessingException | ClassNotFoundException e) {

      handleException(e, returnCode, sqlCode, errorDesc);
    }
  }

  private void handleException(Exception e, String[] returnCode, int[] sqlCode,
      String[] errorDesc) {
    returnCode[0] = "KO";
    sqlCode[0] = 90;
    errorDesc[0] = "SQLERROR";
    logger.error("Error while executing [ CTJ10025 ] SP program", e);
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.ibm.db2.jcc.DB2Driver");
    return DriverManager.getConnection("jdbc:default:connection");
  }

  private void returnValue(CT10025OCommareaWrapper1 returnValue, String[] returnCode, int[] sqlCode,
      String[] errorDesc, byte[][] inout) {
    returnCode[0] = "OK";
    sqlCode[0] = 0;
    errorDesc[0] = "NONE";
    inout[0] = returnValue.getByteBuffer();
  }

  private byte[] getInput(byte[][] inout) {

    return inout[0];
  }

  private void logCommarea(CT10025OCommareaWrapper1 commareaWrapper)
      throws JsonProcessingException {

    if (!logger.isDebugEnabled()) {
      return;
    }

    ObjectMapper objectMapper = new ObjectMapper();
    logger.debug("Retrieved record [ {} ]", objectMapper.writeValueAsString(commareaWrapper));
  }


  private CT10025OCommareaWrapper1 doSelect(Connection connection, CT10025XCommareaWrapper1 input)
      throws SQLException {
//ERRMAK     MOVE 'A2100_OBTENER_DATOS: 1 ' TO LOG_APPL_DATA
//           EXEC SQL
//              SELECT  COD_PROD         ,
//                      COD_SUC_ALTA     ,
//                      COD_MONEDA       ,
//                      IND_EST_CUENTA   ,
//                      IND_SD_LIMITE    ,
//                      COD_SUC_PROPIE   ,
//                      FEC_OPERACION    ,
//                      FEC_VALOR        ,
//                      FEC_CONTABLE     ,
//                      FEC_CONTABLE2    ,
//                      FEC_OPERACION2   ,
//                      FEC_VALOR2       ,
//                      FEC_ULTIMA_OPE3  ,
//                      FEC_MODIFICACION ,
//                      IMP_PROX_RED_LIM ,
//                      POR_REDUCCION    ,
//                      FEC_FIN          ,
//                      FEC_FIN2         ,
//                      FEC_VENCIMIENTO  ,
//                      FEC_OPERACION3   ,
//                      FEC_VALOR3       ,
//                      FEC_CONTABLE5    ,
//                      SAL_CONTABLE     ,
//                      SAL_RETENIDO     ,
//                      IMP_LIM_CTA      ,
//                      IMP_LIM_CUENACTU ,
//                      IMP_LIM_DISPOCTA ,
//                      IND_CONDI_PROPU  ,
//                      IND_VAR_LIQ      ,
//                      FECHA_INICIO     ,
//                      IND_VAR_COMI     ,
//                      FEC_INICIO2      ,
//                      IND_SEGPAQCON    ,
//                      COD_SEG_MERCADO  ,
//                      COD_PAQUETE      ,
//                      COD_CONV_ESPEC   ,
//                      COD_CENTRO       ,
//                      IND_DESAUTORIZACIO,
//                      IND_RETENCIONES  ,
//                      IND_AVISO        ,
//                      IND_ORDEN_PER    ,
//                      IND_SERVICIOS    ,
//                      IND_CTAS_ASOC    ,
//                      IND_DOMICI       ,
//                      IND_CAP_INTERESES,
//                      IND_COND_DISP    ,
//                      IND_LIB_PEDIDA   ,
//                      IND_NIVEL_SEGURIDA,
//                      IND_PER_LIQUES   ,
//                      FEC_INICIO3      ,
//                      IND_CARENCIA_LIQ ,
//                      FEC_INICIO5      ,
//                      IND_PER_REDLIMES ,
//                      FEC_INICIO4      ,
//                      IND_CARENCIA_RED ,
//                      FEC_INICIO6      ,
//                      IND_PLA_EST      ,
//                      FEC_INICIO7      ,
//                      COD_NISEGU       ,
//                      FEC_ULTIMA_OPE4  ,
//                      FEC_ULTIMA_OPE5  ,
//                      IMP_DESCUBIERTO  ,
//                      IND_MAN_CARLIRENO,
//                      IND_MAN_CARREBRENO,
//                      NUM_RENOV_EFEC   ,
//                      IND_NUM_DIA_RES  ,
//                      COD_SECTOR       ,
//                      COD_GARANTIA     ,
//                      COD_FINALI       ,
//                      IND_COMPUTABLE   ,
//                      COD_TIP_PROP     ,
//                      FEC_PROPU        ,
//                      FEC_APLICACION   ,
//                      FEC_APLICACION2  ,
//                      IMP_COMISION     ,
//                      NUM_OPECANAA     ,
//                      NUM_OPEABANAA    ,
//                      IMP_OPCAANACT    ,
//                      IMP_OPABANACT    ,
//                      NUM_DIA_DES_ANO  ,
//                      FEC_INICIO9      ,
//                      IND_TAS_AJUS_AC  ,
//                      FEC_INICIO10     ,
//                      IND_TAS_AJUS_DE  ,
//                      IND_CAR01_EST    ,
//                      FEC_CARACT01     ,
//                      FEC_CONTABLE3    ,
//                      COD_TIP_INTERES  ,
//                      COD_AN_DOC       ,
//                      TIMESTAMP_SIGLO  ,
//                      COD_USU_MODIF    ,
//                      COD_DOMINIO      ,
//                      NUM_NODO         ,
//                      NUM_ULT_MOV      ,
//                      COD_ULT_CHECK    ,
//                      IND_INFRUTTIFERO ,
//                      IND_PARTITA_MINIMA,
//                      FEC_ULT_LIQ_H    ,
//                      FEC_PROX_LIQ_H   ,
//                      SAL_LIQUIDO      ,
//                      SAL_PENDENTE     ,
//                      TOT_BENEFONDI    ,
//                      TOT_PAV_DARE     ,
//                      TOT_PAV_AVERE    ,
//                      TOT_PPR_DARE     ,
//                      TOT_PPR_AVERE    ,
//                      TOT_ASS_ILLIQ    ,
//                      TOT_SBF_ILLIQ    ,
//                      TOT_ALTRI_ILLIQ  ,
//                      TOT_ASS_INDISP   ,
//                      TOT_SBF_INDISP   ,
//                      TOT_ALTRI_INDISP ,
//                      FEC_ANTER_COND   ,
//                      COD_TIPO_PROD    ,
//                      COD_CONVENZIONE
//                INTO  :T08CT005.COD_PROD,
//                      :T08CT005.COD_SUC_ALTA,
//                      :T08CT005.COD_MONEDA,
//                      :T08CT005.IND_EST_CUENTA,
//                      :T08CT005.IND_SD_LIMITE,
//                      :T08CT005.COD_SUC_PROPIE,
//                      :T08CT005.FEC_OPERACION,
//                      :T08CT005.FEC_VALOR,
//                      :T08CT005.FEC_CONTABLE,
//                      :T08CT005.FEC_CONTABLE2,
//                      :T08CT005.FEC_OPERACION2,
//                      :T08CT005.FEC_VALOR2,
//                      :T08CT005.FEC_ULTIMA_OPE3,
//                      :T08CT005.FEC_MODIFICACION,
//                      :T08CT005.IMP_PROX_RED_LIM,
//                      :T08CT005.POR_REDUCCION,
//                      :T08CT005.FEC_FIN ,
//                      :T08CT005.FEC_FIN2,
//                      :T08CT005.FEC_VENCIMIENTO,
//                      :T08CT005.FEC_OPERACION3,
//                      :T08CT005.FEC_VALOR3,
//                      :T08CT005.FEC_CONTABLE5,
//                      :T08CT005.SAL_CONTABLE,
//                      :T08CT005.SAL_RETENIDO,
//                      :T08CT005.IMP_LIM_CTA,
//                      :T08CT005.IMP_LIM_CUENACTU,
//                      :T08CT005.IMP_LIM_DISPOCTA,
//                      :T08CT005.IND_CONDI_PROPU,
//                      :T08CT005.IND_VAR_LIQ,
//                      :T08CT005.FECHA_INICIO,
//                      :T08CT005.IND_VAR_COMI,
//                      :T08CT005.FEC_INICIO2,
//                      :T08CT005.IND_SEGPAQCON,
//                      :T08CT005.COD_SEG_MERCADO,
//                      :T08CT005.COD_PAQUETE,
//                      :T08CT005.COD_CONV_ESPEC,
//                      :T08CT005.COD_CENTRO,
//                      :T08CT005.IND_DESAUTORIZACIO,
//                      :T08CT005.IND_RETENCIONES,
//                      :T08CT005.IND_AVISO,
//                      :T08CT005.IND_ORDEN_PER,
//                      :T08CT005.IND_SERVICIOS,
//                      :T08CT005.IND_CTAS_ASOC,
//                      :T08CT005.IND_DOMICI,
//                      :T08CT005.IND_CAP_INTERESES,
//                      :T08CT005.IND_COND_DISP,
//                      :T08CT005.IND_LIB_PEDIDA,
//                      :T08CT005.IND_NIVEL_SEGURIDA,
//                      :T08CT005.IND_PER_LIQUES,
//                      :T08CT005.FEC_INICIO3,
//                      :T08CT005.IND_CARENCIA_LIQ,
//                      :T08CT005.FEC_INICIO5,
//                      :T08CT005.IND_PER_REDLIMES,
//                      :T08CT005.FEC_INICIO4,
//                      :T08CT005.IND_CARENCIA_RED,
//                      :T08CT005.FEC_INICIO6,
//                      :T08CT005.IND_PLA_EST,
//                      :T08CT005.FEC_INICIO7,
//                      :T08CT005.COD_NISEGU,
//                      :T08CT005.FEC_ULTIMA_OPE4,
//                      :T08CT005.FEC_ULTIMA_OPE5,
//                      :T08CT005.IMP_DESCUBIERTO,
//                      :T08CT005.IND_MAN_CARLIRENO,
//                      :T08CT005.IND_MAN_CARREBRENO,
//                      :T08CT005.NUM_RENOV_EFEC,
//                      :T08CT005.IND_NUM_DIA_RES,
//                      :T08CT005.COD_SECTOR,
//                      :T08CT005.COD_GARANTIA,
//                      :T08CT005.COD_FINALI,
//                      :T08CT005.IND_COMPUTABLE,
//                      :T08CT005.COD_TIP_PROP,
//                      :T08CT005.FEC_PROPU,
//                      :T08CT005.FEC_APLICACION,
//                      :T08CT005.FEC_APLICACION2,
//                      :T08CT005.IMP_COMISION,
//                      :T08CT005.NUM_OPECANAA,
//                      :T08CT005.NUM_OPEABANAA,
//                      :T08CT005.IMP_OPCAANACT,
//                      :T08CT005.IMP_OPABANACT,
//                      :T08CT005.NUM_DIA_DES_ANO,
//                      :T08CT005.FEC_INICIO9,
//                      :T08CT005.IND_TAS_AJUS_AC,
//                      :T08CT005.FEC_INICIO10,
//                      :T08CT005.IND_TAS_AJUS_DE,
//                      :T08CT005.IND_CAR01_EST,
//                      :T08CT005.FEC_CARACT01,
//                      :T08CT005.FEC_CONTABLE3,
//                      :T08CT005.COD_TIP_INTERES,
//                      :T08CT005.COD_AN_DOC,
//                      :T08CT005.TIMESTAMP_SIGLO,
//                      :T08CT005.COD_USU_MODIF,
//                      :T08CT005.COD_DOMINIO,
//                      :T08CT005.NUM_NODO,
//                      :T08CT005.NUM_ULT_MOV,
//                      :T08CT005.COD_ULT_CHECK,
//                      :T08CT005.IND_INFRUTTIFERO,
//                      :T08CT005.IND_PARTITA_MINIMA,
//                      :T08CT005.FEC_ULT_LIQ_H,
//                      :T08CT005.FEC_PROX_LIQ_H,
//                      :T08CT005.SAL_LIQUIDO,
//                      :T08CT005.SAL_PENDENTE,
//                      :T08CT005.TOT_BENEFONDI,
//                      :T08CT005.TOT_PAV_DARE,
//                      :T08CT005.TOT_PAV_AVERE,
//                      :T08CT005.TOT_PPR_DARE,
//                      :T08CT005.TOT_PPR_AVERE,
//                      :T08CT005.TOT_ASS_ILLIQ,
//                      :T08CT005.TOT_SBF_ILLIQ,
//                      :T08CT005.TOT_ALTRI_ILLIQ,
//                      :T08CT005.TOT_ASS_INDISP,
//                      :T08CT005.TOT_SBF_INDISP,
//                      :T08CT005.TOT_ALTRI_INDISP,
//                      :T08CT005.FEC_ANTER_COND,
//                      :T08CT005.COD_TIPO_PROD,
//                      :T08CT005.COD_CONVENZIONE
//              FROM T08CT005
//              WHERE COD_TIP_EXPE      = :T08CT005.COD_TIP_EXPE   AND
//                    NUM_CTA_INT       = :T08CT005.NUM_CTA_INT    AND
//                    COD_EMPRESA       = :T08CT005.COD_EMPRESA
//           END_EXEC.

    String sql = "SELECT  "
        + "      COD_PROD              ,    \n"
        + "      COD_SUC_ALTA          ,    \n"
        + "      COD_MONEDA            ,    \n"
        + "      IND_EST_CUENTA        ,    \n"
        + "      IND_SD_LIMITE         ,    \n"
        + "      COD_SUC_PROPIE        ,    \n"
        + "      FEC_OPERACION         ,    \n"
        + "      FEC_VALOR             ,    \n"
        + "      FEC_CONTABLE          ,    \n"
        + "      FEC_CONTABLE2         ,    \n"
        + "      FEC_OPERACION2        ,    \n"
        + "      FEC_VALOR2            ,    \n"
        + "      FEC_ULTIMA_OPE3       ,    \n"
        + "      FEC_MODIFICACION      ,    \n"
        + "      IMP_PROX_RED_LIM      ,    \n"
        + "      POR_REDUCCION         ,    \n"
        + "      FEC_FIN               ,    \n"
        + "      FEC_FIN2              ,    \n"
        + "      FEC_VENCIMIENTO       ,    \n"
        + "      FEC_OPERACION3        ,    \n"
        + "      FEC_VALOR3            ,    \n"
        + "      FEC_CONTABLE5         ,    \n"
        + "      SAL_CONTABLE          ,    \n"
        + "      SAL_RETENIDO          ,    \n"
        + "      IMP_LIM_CTA           ,    \n"
        + "      IMP_LIM_CUENACTU      ,    \n"
        + "      IMP_LIM_DISPOCTA      ,    \n"
        + "      IND_CONDI_PROPU       ,    \n"
        + "      IND_VAR_LIQ           ,    \n"
        + "      FECHA_INICIO          ,    \n"
        + "      IND_VAR_COMI          ,    \n"
        + "      FEC_INICIO2           ,    \n"
        + "      IND_SEGPAQCON         ,    \n"
        + "      COD_SEG_MERCADO       ,    \n"
        + "      COD_PAQUETE           ,    \n"
        + "      COD_CONV_ESPEC        ,    \n"
        + "      COD_CENTRO            ,    \n"
        + "      IND_DESAUTORIZACIO    ,    \n"
        + "      IND_RETENCIONES       ,    \n"
        + "      IND_AVISO             ,    \n"
        + "      IND_ORDEN_PER         ,    \n"
        + "      IND_SERVICIOS         ,    \n"
        + "      IND_CTAS_ASOC         ,    \n"
        + "      IND_DOMICI            ,    \n"
        + "      IND_CAP_INTERESES     ,    \n"
        + "      IND_COND_DISP         ,    \n"
        + "      IND_LIB_PEDIDA        ,    \n"
        + "      IND_NIVEL_SEGURIDA    ,    \n"
        + "      IND_PER_LIQUES        ,    \n"
        + "      FEC_INICIO3           ,    \n"
        + "      IND_CARENCIA_LIQ      ,    \n"
        + "      FEC_INICIO5           ,    \n"
        + "      IND_PER_REDLIMES      ,    \n"
        + "      FEC_INICIO4           ,    \n"
        + "      IND_CARENCIA_RED      ,    \n"
        + "      FEC_INICIO6           ,    \n"
        + "      IND_PLA_EST           ,    \n"
        + "      FEC_INICIO7           ,    \n"
        + "      COD_NISEGU            ,    \n"
        + "      FEC_ULTIMA_OPE4       ,    \n"
        + "      FEC_ULTIMA_OPE5       ,    \n"
        + "      IMP_DESCUBIERTO       ,    \n"
        + "      IND_MAN_CARLIRENO     ,    \n"
        + "      IND_MAN_CARREBRENO    ,    \n"
        + "      NUM_RENOV_EFEC        ,    \n"
        + "      IND_NUM_DIA_RES       ,    \n"
        + "      COD_SECTOR            ,    \n"
        + "      COD_GARANTIA          ,    \n"
        + "      COD_FINALI            ,    \n"
        + "      IND_COMPUTABLE        ,    \n"
        + "      COD_TIP_PROP          ,    \n"
        + "      FEC_PROPU             ,    \n"
        + "      FEC_APLICACION        ,    \n"
        + "      FEC_APLICACION2       ,    \n"
        + "      IMP_COMISION          ,    \n"
        + "      NUM_OPECANAA          ,    \n"
        + "      NUM_OPEABANAA         ,    \n"
        + "      IMP_OPCAANACT         ,    \n"
        + "      IMP_OPABANACT         ,    \n"
        + "      NUM_DIA_DES_ANO       ,    \n"
        + "      FEC_INICIO9           ,    \n"
        + "      IND_TAS_AJUS_AC       ,    \n"
        + "      FEC_INICIO10          ,    \n"
        + "      IND_TAS_AJUS_DE       ,    \n"
        + "      IND_CAR01_EST         ,    \n"
        + "      FEC_CARACT01          ,    \n"
        + "      FEC_CONTABLE3         ,    \n"
        + "      COD_TIP_INTERES       ,    \n"
        + "      COD_AN_DOC            ,    \n"
        + "      TIMESTAMP_SIGLO       ,    \n"
        + "      COD_USU_MODIF         ,    \n"
        + "      COD_DOMINIO           ,    \n"
        + "      NUM_NODO              ,    \n"
        + "      NUM_ULT_MOV           ,    \n"
        + "      COD_ULT_CHECK         ,    \n"
        + "      IND_INFRUTTIFERO      ,    \n"
        + "      IND_PARTITA_MINIMA    ,    \n"
        + "      FEC_ULT_LIQ_H         ,    \n"
        + "      FEC_PROX_LIQ_H        ,    \n"
        + "      SAL_LIQUIDO           ,    \n"
        + "      SAL_PENDENTE          ,    \n"
        + "      TOT_BENEFONDI         ,    \n"
        + "      TOT_PAV_DARE          ,    \n"
        + "      TOT_PAV_AVERE         ,    \n"
        + "      TOT_PPR_DARE          ,    \n"
        + "      TOT_PPR_AVERE         ,    \n"
        + "      TOT_ASS_ILLIQ         ,    \n"
        + "      TOT_SBF_ILLIQ         ,    \n"
        + "      TOT_ALTRI_ILLIQ       ,    \n"
        + "      TOT_ASS_INDISP        ,    \n"
        + "      TOT_SBF_INDISP        ,    \n"
        + "      TOT_ALTRI_INDISP      ,    \n"
        + "      FEC_ANTER_COND        ,    \n"
        + "      COD_TIPO_PROD         ,    \n"
        + "      COD_CONVENZIONE            \n"
        + "FROM "
        + "      STS3.T08CT005              \n"
        + "WHERE "
        + "      COD_TIP_EXPE      = ?   AND  \n"
        + "      NUM_CTA_INT       = ?   AND  \n"
        + "      COD_EMPRESA       = ?";

    PreparedStatement stmt = connection.prepareStatement(sql);
    stmt.setString(1, input.getCodTipExpe());
    stmt.setInt(2, input.getNumCtaInt());
    stmt.setString(3, input.getCodEmpresa());

    ResultSet rs = stmt.executeQuery();

    final CT10025OCommareaWrapper1 returnValue = new CT10025OCommareaWrapper1();
    final boolean hasNext = rs.next();

    if (!hasNext) {
//               MOVE '00001'    TO COD-MSJE-RETORNO OF OUTPUT-TXN
      returnValue.setCodMsjeRetorno("00001");
//               MOVE 'A2100'        TO NOM-RUTINA   OF CT0009P
//               MOVE 'T08CT005'     TO NOM-TABLA    OF CT0009P
//               MOVE LT-A-CTS10025A
//      *        MOVE 'LA CUENTA NO EXISTE EN TABLA'
//                                   TO DEL-INCID    OF CT0009P
      return returnValue;
    }

//           MOVE COD-PROD        OF T08CT005
//                             TO  COD-PROD        OF RES-MEN-CT1002O
    returnValue.setCodProd(rs.getString(1));
//           MOVE COD-SUC-ALTA    OF T08CT005
//                             TO  COD-SUC-ALTA    OF RES-MEN-CT1002O
    returnValue.setCodSucAlta(rs.getInt(2));
//           MOVE COD-MONEDA      OF T08CT005
//                             TO  COD-MONEDA      OF RES-MEN-CT1002O
    returnValue.setCodMoneda(rs.getString(3));
//           MOVE IND-EST-CUENTA  OF T08CT005
//                             TO  IND-EST-CUENTA  OF RES-MEN-CT1002O
    returnValue.setIndEstCuenta(rs.getString(4));
//           MOVE IND-SD-LIMITE   OF T08CT005
//                             TO  IND-SD-LIMITE   OF RES-MEN-CT1002O
    returnValue.setIndSdLimite(rs.getString(5));
//           MOVE COD-SUC-PROPIE  OF T08CT005
//                             TO  COD-SUC-PROPIE  OF RES-MEN-CT1002O
    returnValue.setCodSucPropie(rs.getInt(6));
//           MOVE FEC-OPERACION   OF T08CT005
//                             TO  FEM-OPERACION   OF RES-MEN-CT1002O
    returnValue.setFemOperacion(rs.getString(7));
//           MOVE FEC-VALOR       OF T08CT005
//                             TO  FEM-VALOR       OF RES-MEN-CT1002O
    returnValue.setFemValor(rs.getString(8));
//           MOVE FEC-CONTABLE    OF T08CT005
//                             TO  FEM-CONTABLE    OF RES-MEN-CT1002O
    returnValue.setFemContable(rs.getString(9));
//           MOVE FEC-CONTABLE2   OF T08CT005
//                             TO  FEM-CONTA2      OF RES-MEN-CT1002O
    returnValue.setFemConta2(rs.getString(10));
    returnValue.setFemOperacion2(rs.getString(11));
    returnValue.setFemValor2(rs.getString(12));
    returnValue.setFemUltimaOpe3(rs.getString(13));
    returnValue.setFemModificacion(rs.getString(14));
    returnValue.setImpProxRedLim(rs.getBigDecimal(15));
    returnValue.setPorReduccion(rs.getBigDecimal(16));
    returnValue.setFemFin(rs.getString(17));
    returnValue.setFemFin2(rs.getString(18));
    returnValue.setFemVencimiento(rs.getString(19));
    returnValue.setFemOperacion3(rs.getString(20));
    returnValue.setFemValor3(rs.getString(21));
    returnValue.setFemConta5(rs.getString(22));
    returnValue.setSalcontable(rs.getBigDecimal(23));
    returnValue.setSalretenido(rs.getBigDecimal(24));
    returnValue.setImpLimCta(rs.getBigDecimal(25));
    returnValue.setImpLimCuenactu(rs.getBigDecimal(26));
    returnValue.setImpLimDispocta(rs.getBigDecimal(27));
    returnValue.setIndCondiPropu(rs.getString(28));
    returnValue.setIndVarLiq(rs.getString(29));
    returnValue.setFemInicio(rs.getString(30));
    returnValue.setIndVarComi(rs.getString(31));
    returnValue.setFemInicio2(rs.getString(32));
    returnValue.setIndSegpaqcon(rs.getInt(33));
    returnValue.setCodSegMercado(rs.getString(34));
    returnValue.setCodPaquete(rs.getString(35));
    returnValue.setCodConvEspec(rs.getString(36));
    returnValue.setCodCentro(rs.getString(37));
    returnValue.setIndDesautorizaciones(rs.getString(38));
    returnValue.setIndRetenciones(rs.getString(39));
    returnValue.setIndAviso(rs.getString(40));
    returnValue.setIndOrdenPer(rs.getString(41));
    returnValue.setIndServicios(rs.getString(42));
    returnValue.setIndCtasAsoc(rs.getString(43));
    returnValue.setIndDomici(rs.getString(44));
    returnValue.setIndCapIntereses(rs.getString(45));
    returnValue.setIndCondDisp(rs.getString(46));
    returnValue.setIndLibPedida(rs.getString(47));
    returnValue.setIndNivelSeguridad(rs.getString(48));
    returnValue.setIndPerLiques(rs.getString(49));
    returnValue.setFemInicio3(rs.getString(50));
    returnValue.setIndCarenciaLiq(rs.getString(51));
    returnValue.setFemInicio5(rs.getString(52));
    returnValue.setIndPerRedlimes(rs.getString(53));
    returnValue.setFemInicio4(rs.getString(54));
    returnValue.setIndCarenciaRed(rs.getString(55));
    returnValue.setFemInicio6(rs.getString(56));
    returnValue.setIndPlaEst(rs.getString(57));
    returnValue.setFemInicio7(rs.getString(58));
    returnValue.setCodNisegu(rs.getString(59));
    returnValue.setFemUltimaOpe4(rs.getString(60));
    returnValue.setFemUltimaOpe5(rs.getString(61));
    returnValue.setImpDescubierto(rs.getBigDecimal(62));
    returnValue.setIndManCarlireno(rs.getString(63));
    returnValue.setIndManCarrebreno(rs.getString(64));
    returnValue.setNumRenovEfec(rs.getInt(65));
    returnValue.setIndNumDiaRes(rs.getString(66));
    returnValue.setCodSector(rs.getString(67));
    returnValue.setCodGarantia(rs.getString(68));
    returnValue.setCodFinali(rs.getString(69));
    returnValue.setIndComputable(rs.getString(70));
    returnValue.setCodTipProp(rs.getString(71));
    returnValue.setFemPropu(rs.getString(72));
    returnValue.setFemAplicacion(rs.getString(73));
    returnValue.setFemAplicacion2(rs.getString(74));
    returnValue.setImpcomision(rs.getBigDecimal(75));
    returnValue.setNumOpecanna(rs.getInt(76));
    returnValue.setNumOpeabanna(rs.getInt(77));
    returnValue.setImpOpcaanact(rs.getBigDecimal(78));
    returnValue.setImpOpabanact(rs.getBigDecimal(79));
    returnValue.setNumDiaDesAno(rs.getInt(80));
    returnValue.setFemInicio9(rs.getString(81));
    returnValue.setIndTasAjusAc(rs.getString(82));
    returnValue.setFemInicio10(rs.getString(83));
    returnValue.setIndTasAjusDe(rs.getString(84));
    returnValue.setIndCar01Est(rs.getString(85));
    returnValue.setFemCaract01(rs.getString(86));
    returnValue.setFemConta3(rs.getString(87));
    returnValue.setCodTipInteres(rs.getString(88));
    returnValue.setCodAnDoc(rs.getString(89));
    returnValue.setTimestampSiglom(rs.getString(90));
    returnValue.setCodUsuModif(rs.getString(91));
    returnValue.setCodDominio(rs.getInt(92));
    returnValue.setNumNodo(rs.getInt(93));
    returnValue.setNumUltMov(rs.getInt(94));
    returnValue.setCodUltCheck(rs.getString(95));
    returnValue.setIndInfruttifero(rs.getString(96));
    returnValue.setIndPartitaMinima(rs.getString(97));
    returnValue.setFemUltLiqH(rs.getString(98));
    returnValue.setFemProxLiqH(rs.getString(99));
    returnValue.setSalLiquido(rs.getBigDecimal(100));
    returnValue.setSalPendente(rs.getBigDecimal(101));
    returnValue.setTotBenefondi(rs.getBigDecimal(102));
    returnValue.setTotPavDare(rs.getBigDecimal(103));
    returnValue.setTotPavAvere(rs.getBigDecimal(104));
    returnValue.setTotPprDare(rs.getBigDecimal(105));
    returnValue.setTotPprAvere(rs.getBigDecimal(106));
    returnValue.setTotAssIlliq(rs.getBigDecimal(107));
    returnValue.setTotSbfIlliq(rs.getBigDecimal(108));
    returnValue.setTotAltriIlliq(rs.getBigDecimal(109));
    returnValue.setTotAssIndisp(rs.getBigDecimal(110));
    returnValue.setTotSbfIndisp(rs.getBigDecimal(111));
    returnValue.setTotAltriIndisp(rs.getBigDecimal(112));
    returnValue.setFemAnterCond(rs.getString(113));
//      returnValue.setCodTipoProd(rs.getString(114));

//980518     MOVE COD-CONVENZIONE OF T08CT005
//                             TO  FEM-ULTIMA-OPE3 OF RES-MEN-CT1002O
    returnValue.setFemUltimaOpe3(rs.getString(115));

    return returnValue;
  }
}
