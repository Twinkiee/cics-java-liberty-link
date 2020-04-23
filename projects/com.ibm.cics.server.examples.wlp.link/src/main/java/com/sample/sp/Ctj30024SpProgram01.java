package com.sample.sp;


import com.ibm.cics.server.CCSIDErrorException;
import com.ibm.cics.server.ChannelErrorException;
import com.ibm.cics.server.CodePageErrorException;
import com.ibm.cics.server.ContainerErrorException;
import com.ibm.cics.server.InvalidRequestException;
import com.sample.commarea.CT30024XCommareaWrapper1;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ctj30024SpProgram01 {

  public static final int ON_LINE = 0;
  public static final int BATCH = 1;
  public static final int REUTIL_ON = 2;

  private static final Logger logger = LoggerFactory.getLogger(Ctj30024SpProgram01.class);

  public void execute(
      String[] returnCode
      , int[] sqlCode
      , String[] errorDesc
      , byte[][] inout) {
    logger.info("Executing [ CTJ30024 ] CICS program");

    long time = System.nanoTime();

    byte[] input = getInput(inout);

    try (Connection connection = getConnection()) {
      connection.setAutoCommit(false);

      CT30024XCommareaWrapper1 wrapper = new CT30024XCommareaWrapper1(input);

      logCommarea(wrapper);

      connection.setAutoCommit(false);

      doUpdate(connection, wrapper);

      returnValue(wrapper, returnCode, sqlCode, errorDesc, inout);

      logger.info("[ CTJ3024 ] SP program executed in [ {} ] ns", System.nanoTime() - time);
    } catch (SQLException | ClassNotFoundException e) {

      handleException(e, returnCode, sqlCode, errorDesc);
    }
  }

  private byte[] getInput(byte[][] inout) {

    return inout[0];
  }

  private Connection getConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.ibm.db2.jcc.DB2Driver");
    return DriverManager.getConnection("jdbc:default:connection");
  }

  private void logCommarea(CT30024XCommareaWrapper1 commareaWrapper) {

    if (!logger.isDebugEnabled()) {
      return;
    }

    logger.debug("CT30024X commarea [ {} ]", commareaWrapper);
  }

  private void returnValue(CT30024XCommareaWrapper1 returnValue, String[] returnCode, int[] sqlCode,
      String[] errorDesc, byte[][] inout) {
    returnCode[0] = "OK";
    sqlCode[0] = 0;
    errorDesc[0] = "NONE";
    inout[0] = returnValue.getByteBuffer();
  }

  private void handleException(Exception e, String[] returnCode, int[] sqlCode,
      String[] errorDesc) {
    returnCode[0] = "KO";
    sqlCode[0] = 90;
    errorDesc[0] = "SQLERROR";
    logger.error("Error while executing [ CTJ30024 ] SP program", e);
  }

  private void doUpdate(Connection connection,
      CT30024XCommareaWrapper1 wrapper) throws SQLException {

//          *--- DATOS DE ARQUITECTURA - GLOBALES
//          *--- ARCHITECTURE DATA - GLOBALS
//
//          **CT2001N1 BEGIN
//          ** LA DATA CONTABILE VIENE VALORIZZATA DAL CHIAMANTE
//               IF FEC-CONTABLE OF MSGINSMOVE = SPACES
//                  MOVE AQBF-FEM-CONTABLE  TO FEC-CONTABLE OF T08CT006
//                 ELSE
//                  MOVE FEC-CONTABLE OF MSGINSMOVE
//                                          TO FEC-CONTABLE OF T08CT006
//               END-IF.
    if (wrapper.getFecContable() == null || wrapper.getFecContable().isEmpty()) {
      wrapper.setFecContable(wrapper.getAqbfFemContable());
    }

//    990329** LA DATA OPERAZIONE VIENE VALORIZZATA DAL CHIAMANTE
//    990329     IF FEC-OPERACION OF MSGINSMOVE = SPACES
//    990329         MOVE AQBF-FEM-OPERACION TO FEC-OPERACION OF T08CT006
//    990329        ELSE
//    990329         MOVE FEC-OPERACION OF MSGINSMOVE
//    990329                                 TO FEC-OPERACION OF T08CT006
//    990329     END-IF.
    if (wrapper.getFecOperacion() == null || wrapper.getFecOperacion().isEmpty()) {
      wrapper.setFecOperacion(wrapper.getAqbfFemOperacion());
    }

//    990329*    MOVE AQBF-FEM-OPERACION TO FEC-OPERACION   OF T08CT006
//               MOVE AQBF-COD-USUARIO   TO COD-USU-ALTA    OF T08CT006
//                                          COD-USU-MODIF   OF T08CT006.
    wrapper.setCodUsuAlta(wrapper.getAqbfCodUsuario());
    wrapper.setCodUsuModif(wrapper.getAqbfCodUsuario());

//    980714     IF  BATCH OF AQBF-IND-ON-BATCH
//    980714         MOVE COD-CENT-ORIG OF INPUT-TXN
//    980714                                 TO COD-CENT-ORIG   OF T08CT006
//    980714     ELSE
//    980714         MOVE AQBF-COD-CENTRO    TO COD-CENT-ORIG  OF T08CT006
//    980714     END-IF.
    try {
      if (wrapper.getAqbfIndOnBatch() != BATCH) {
        wrapper.setCodCentOrig(wrapper.getAqbfCodCentro());
      }

    } catch (IllegalArgumentException iae) {
      logger.debug("AQBF-IND-ON-BATCH not set. Setting COD-CENT-ORIG to AQBF-COD-CENTRO");
      wrapper.setCodCentOrig(wrapper.getAqbfCodCentro());
    }

//               MOVE AQBF-COD-DOMINIO   TO COD-DOMINIO     OF T08CT006
    try {
      wrapper.setCodDominio(wrapper.getAqbfCodDominio());
    } catch (IllegalArgumentException iae) {
      logger.debug("Skipping setting value for AQBF-COD-DOMINIO to COD-DOMINIO");
    }
//               MOVE AQBF-NUM-NODO      TO NUM-NODO        OF T08CT006
    try {
      wrapper.setNumNodo(wrapper.getAqbfNumNodo());
    } catch (IllegalArgumentException iae) {
      logger.debug("Skipping setting value for AQBF-NUM-NODO to NUM-NODO");
    }
//               MOVE AQBF-NUM-ULT       TO NUM-OPERACION   OF T08CT006
    try {
      wrapper.setNumOperacion(wrapper.getAqbfNumUlt());
    } catch (IllegalArgumentException iae) {
      logger.debug("Skipping setting value for AQBF-NUM-ULT to NUM-OPERACION");
    }
//          * 10DS1095 - INIZIO
//          *    MOVE AQBF-COD-OPER-ENC  TO NUM-OPER-ENLAZA OF T08CT006
//               MOVE NUM-OPER-ENLAZA OF INPUT-TXN
//                                       TO NUM-OPER-ENLAZA OF T08CT006
    wrapper.setNumOperEnlaza(wrapper.getNumOperEnlaza());
//          * 10DS1095 - FINE
//    980604     MOVE AQBF-NUM-DE-PUESTO TO NUM-DE-PUESTO   OF T08CT006.
    try {
      wrapper.setNumDePuesto(wrapper.getAqbfNumDePuesto());
    } catch (IllegalArgumentException iae) {
      logger.debug("Skipping setting value for AQBF-NUM-DE-PUESTO to NUM-DE-PUESTO");
    }
//          **CT2002N1 END
//
//      *--- INDICADOR DE OPERACION ENCADENADA
//      *--- INDICATOR OF CHAINED OPERATION
//      **   IF AQBF-COD-OPER-ENC > 0 THEN
//      *------ SI ENCADENADA
//      *------ IT IS CHAINED
//      **      MOVE '1' TO IND-OPER-ENLAZADA OF T08CT006
//      **   ELSE
//      *------ NO ENCADENADA
//      *------ IT IS NOT CHAINED
//      **      MOVE '0' TO IND-OPER-ENLAZADA OF T08CT006
//      **   END-IF
//      ********* MODIFICA DI IND-OPER-ENLAZADA E' UN INDICATORE
//      ********* CHE DICE SE IL MOVIMETNO E' STATO GIRATO SU UN
//      ********* UN ALTRO CONTO
//           IF IND-OPER-ENLAZADA OF INPUT-TXN = SPACE
//              MOVE '0' TO IND-OPER-ENLAZADA OF T08CT006
//           ELSE
//              MOVE  IND-OPER-ENLAZADA OF INPUT-TXN
//                                     TO IND-OPER-ENLAZADA OF T08CT006
//           END-IF.

    if (wrapper.getIndOperEnlazada() == null || wrapper.getIndOperEnlazada().isEmpty()) {
      wrapper.setIndOperEnlazada("0");
    }
//
//           MOVE '01.01.0001'         TO  FEC-MODIFICACION OF T08CT006
    wrapper.setFecModificacion("01.01.0001");

    wrapper.setFecVencimiento("9999-01-01");

    wrapper.setFechaInicio("0001-01-01");

//           MOVE NUM-CTA-INT    OF MSGINSMOVE TO WS-NUM-CTA-INT
//           MOVE COD-TIP-EXPE   OF MSGINSMOVE TO WS-COD-TIP-EXPE
//           MOVE COD-EMPRESA    OF MSGINSMOVE TO WS-COD-EMPRESA
//           MOVE IND-NATURALEZA OF MSGINSMOVE TO WS-NATURALEZA
//           MOVE 0                            TO WS-SALDO.
//
//      *CT2003N1-INIZIO
//           MOVE  ZERO TO IND-EXTRA-ENVI OF T08CT006.
//      *CT2003N1-FINE
//      *--- RPM2003 - 15/12/94
//      *--- OBTENER ULTIMO SALDO
//      *--- OBTAINING LAST BALANCE
//           PERFORM A2121-OBTENER-SALDO.
//
//      *    EXEC SQL
//      *         SELECT SAL_DESOPER
//      *         INTO :WS-SALDO
//      *         FROM T08CT006
//      *         WHERE NUM_CTA_INT    = :WS-NUM-CTA-INT    AND
//      *               COD_TIP_EXPE   = :WS-COD-TIP-EXPE   AND
//      *               COD_EMPRESA    = :WS-COD-EMPRESA    AND
//      *               TIMESTAMP_ALTA = ( SELECT MAX(TIMESTAMP_ALTA)
//      *                                  FROM T08CT006
//      *                                  WHERE NUM_CTA_INT =
//      *                                             :WS-NUM-CTA-INT  AND
//      *                                        COD_TIP_EXPE =
//      *                                             :WS-COD-TIP-EXPE AND
//      *                                        COD_EMPRESA  =
//      *                                             :WS-COD-EMPRESA )
//      *    END-EXEC.
//
//ERRMAK     MOVE 'A2120-INSERTAR-REGISTRO: 9 ' TO LOG-APPL-DATA
//ROLLBK     MOVE 'Y' TO FLAG-ROLLBACK OF PABHI
//           EXEC SQL
//                INSERT
//                INTO T08CT006
//                     (COD_TIP_EXPE            ,
//                      NUM_CTA_INT             ,
//                      COD_EMPRESA             ,
//                      FEC_OPERACION           ,
//                      TIMESTAMP_ALTA          ,
//                      COD_OPERACION           ,
//                      COD_REFER               ,
//                      DEC_REF_AMPLIADA        ,
//                      IMP_MOVIMIENTO          ,
//                      IND_NATURALEZA          ,
//                      FEC_CONTABLE            ,
//                      FEC_VALOR               ,
//                      SAL_DESOPER             ,
//                      TXT_MOVIMIENTO          ,
//                      NUM_OPERACION           ,
//                      NUM_OPER_ENLAZA         ,
//                      IND_OPER_ENLAZADA       ,
//                      IND_PTE_LIBRETA         ,
//                      IND_EXTRA_ENVI          ,
//                      NUMERO_DOCUMENTO        ,
//                      IND_MOVAN               ,
//                      IND_CONDO               ,
//                      IND_ORIGEN_MOV          ,
//                      IND_SOPORTE             ,
//                      COD_TIPO_INTERVEN       ,
//                      NOMBRE                  ,
//                      COD_IDENTIFICA          ,
//                      FEC_VENCIMIENTO         ,
//                      COD_ENT_CP              ,
//                      COD_SUC_CP              ,
//                      COD_DIG_CONTROLCP       ,
//                      NUM_CUENTA_CP           ,
//                      FECHA_INICIO            ,
//                      FEC_MODIFICACION        ,
//                      COD_USU_ALTA            ,
//                      COD_CENT_ORIG           ,
//                      COD_DESFI               ,
//                      NUM_CTA_CONT            ,
//                      COD_REFERENCIA_OPE      ,
//                      IND_TRUNC               ,
//                      COD_NUM_DOC_OFICIA      ,
//                      COD_ESTSI               ,
//                      COD_PAIS                ,
//                      NUM_IMPOSICION          ,
//                      SAL_ACTUAL              ,
//                      TIMESTAMP_SIGLO         ,
//                      COD_USU_MODIF           ,
//                      COD_DOMINIO             ,
//                      NUM_NODO                ,
//                      IND_MOD_TABLA           ,
//      **CT2001N1 BEGIN
//                      NUM_MOVIMENTO           ,
//                      IMP_ORIGINARIO          ,
//                      COD_MONEDA_ORIG         ,
//                      IMP_CONTA               ,
//                      IMP_COMMISSIONI         ,
//                      IMP_SPESE               ,
//                      IMP_SPESE_RECLAM        ,
//                      COD_GRUPPO_SPESA        ,
//                      FEC_DISPONIBILITA       ,
//                      IND_INVIO_CONTAB        ,
//                      IND_MOVST               ,
//                      IND_MOVFOR_VAL          ,
//                      IND_MOVFOR_SCO          ,
//                      IND_MOVFOR_BLO          ,
//                      IND_MOVFOR_IMP          ,
//                      IND_MOVFOR_RIS          ,
//                      COD_CHECK               ,
//                      NUM_REL_CLIENTE         ,
//                      NUM_MOVST               ,
//                      NUM_DE_PUESTO           ,
//                      TIP_DISPONIBILITA       ,
//                      TIP_LIQUIDITA           ,
//                      COD_CANALE              ,
//                      COD_PROC_ORIG )
//      **CT2001N1 END
//                VALUES (:T08CT006.COD-TIP-EXPE            ,
//                        :T08CT006.NUM-CTA-INT             ,
//                        :T08CT006.COD-EMPRESA             ,
//                        :T08CT006.FEC-OPERACION           ,
//                        CURRENT TIMESTAMP                 ,
//                        :T08CT006.COD-OPERACION           ,
//                        :T08CT006.COD-REFER               ,
//                        :T08CT006.DEC-REF-AMPLIADA        ,
//                        :T08CT006.IMP-MOVIMIENTO          ,
//                        :T08CT006.IND-NATURALEZA          ,
//                        :T08CT006.FEC-CONTABLE            ,
//                        :T08CT006.FEC-VALOR               ,
//                        :WS-SALDO                         ,
//                        :T08CT006.TXT-MOVIMIENTO          ,
//                        :T08CT006.NUM-OPERACION           ,
//                        :T08CT006.NUM-OPER-ENLAZA         ,
//                        :T08CT006.IND-OPER-ENLAZADA       ,
//                        :T08CT006.IND-PTE-LIBRETA         ,
//                        :T08CT006.IND-EXTRA-ENVI          ,
//                        :T08CT006.NUMERO-DOCUMENTO        ,
//                        :T08CT006.IND-MOVAN               ,
//                        :T08CT006.IND-CONDO               ,
//                        :T08CT006.IND-ORIGEN-MOV          ,
//                        :T08CT006.IND-SOPORTE             ,
//                        :T08CT006.COD-TIPO-INTERVEN       ,
//                        :T08CT006.NOMBRE                  ,
//                        :T08CT006.COD-IDENTIFICA          ,
//                        :T08CT006.FEC-VENCIMIENTO         ,
//                        :T08CT006.COD-ENT-CP              ,
//                        :T08CT006.COD-SUC-CP              ,
//                        :T08CT006.COD-DIG-CONTROLCP       ,
//                        :T08CT006.NUM-CUENTA-CP           ,
//                        :T08CT006.FECHA-INICIO            ,
//                        :T08CT006.FEC-MODIFICACION        ,
//                        :T08CT006.COD-USU-ALTA            ,
//                        :T08CT006.COD-CENT-ORIG           ,
//                        :T08CT006.COD-DESFI               ,
//                        :T08CT006.NUM-CTA-CONT            ,
//                        :T08CT006.COD-REFERENCIA-OPE      ,
//                        :T08CT006.IND-TRUNC               ,
//                        :T08CT006.COD-NUM-DOC-OFICIA      ,
//                        :T08CT006.COD-ESTSI               ,
//                        :T08CT006.COD-PAIS                ,
//                        :T08CT006.NUM-IMPOSICION          ,
//                        :T08CT006.SAL-ACTUAL              ,
//                        CURRENT TIMESTAMP                 ,
//                        :T08CT006.COD-USU-MODIF           ,
//                        :T08CT006.COD-DOMINIO             ,
//                        :T08CT006.NUM-NODO                ,
//                        :T08CT006.IND-MOD-TABLA           ,
//      **CT2001N1 BEGIN
//                        :T08CT006.NUM-MOVIMENTO           ,
//                        :T08CT006.IMP-ORIGINARIO          ,
//                        :T08CT006.COD-MONEDA-ORIG         ,
//                        :T08CT006.IMP-CONTA               ,
//                        :T08CT006.IMP-COMMISSIONI        ,
//                        :T08CT006.IMP-SPESE              ,
//                        :T08CT006.IMP-SPESE-RECLAM       ,
//                        :T08CT006.COD-GRUPPO-SPESA       ,
//                        :T08CT006.FEC-DISPONIBILITA      ,
//                        :T08CT006.IND-INVIO-CONTAB       ,
//                        :T08CT006.IND-MOVST              ,
//                        :T08CT006.IND-MOVFOR-VAL         ,
//                        :T08CT006.IND-MOVFOR-SCO         ,
//                        :T08CT006.IND-MOVFOR-BLO         ,
//                        :T08CT006.IND-MOVFOR-IMP         ,
//                        :T08CT006.IND-MOVFOR-RIS         ,
//                        :T08CT006.COD-CHECK              ,
//                        :T08CT006.NUM-REL-CLIENTE        ,
//                        :T08CT006.NUM-MOVST              ,
//                        :T08CT006.NUM-DE-PUESTO          ,
//                        :T08CT006.TIP-DISPONIBILITA      ,
//                        :T08CT006.TIP-LIQUIDITA          ,
//                        :T08CT006.COD-CANALE             ,
//                        :T08CT006.COD-PROC-ORIG          )
//      **CT2001N1 END
//           END-EXEC.
    String sql = "INSERT\n"
        + "                INTO STS3.T08CT006\n"
        + "                     (COD_TIP_EXPE                        ,\n"
        + "                      NUM_CTA_INT                         ,\n"
        + "                      COD_EMPRESA                         ,\n"
        + "                      FEC_OPERACION                       ,\n"
        + "                      TIMESTAMP_ALTA                      ,\n"
        + "                      COD_OPERACION                       ,\n"
        + "                      COD_REFER                           ,\n"
        + "                      DEC_REF_AMPLIADA                    ,\n"
        + "                      IMP_MOVIMIENTO                      ,\n"
        + "                      IND_NATURALEZA                      ,\n"
        + "                      FEC_CONTABLE                        ,\n"
        + "                      FEC_VALOR                           ,\n"
        + "                      SAL_DESOPER                         ,\n"
        + "                      TXT_MOVIMIENTO                      ,\n"
        + "                      NUM_OPERACION                       ,\n"
        + "                      NUM_OPER_ENLAZA                     ,\n"
        + "                      IND_OPER_ENLAZADA                   ,\n"
        + "                      IND_PTE_LIBRETA                     ,\n"
        + "                      IND_EXTRA_ENVI                      ,\n"
        + "                      NUMERO_DOCUMENTO                    ,\n"
        + "                      IND_MOVAN                           ,\n"
        + "                      IND_CONDO                           ,\n"
        + "                      IND_ORIGEN_MOV                      ,\n"
        + "                      IND_SOPORTE                         ,\n"
        + "                      COD_TIPO_INTERVEN                   ,\n"
        + "                      NOMBRE                              ,\n"
        + "                      COD_IDENTIFICA                      ,\n"
        + "                      FEC_VENCIMIENTO                     ,\n"
        + "                      COD_ENT_CP                          ,\n"
        + "                      COD_SUC_CP                          ,\n"
        + "                      COD_DIG_CONTROLCP                   ,\n"
        + "                      NUM_CUENTA_CP                       ,\n"
        + "                      FECHA_INICIO                        ,\n"
        + "                      FEC_MODIFICACION                    ,\n"
        + "                      COD_USU_ALTA                        ,\n"
        + "                      COD_CENT_ORIG                       ,\n"
        + "                      COD_DESFI                           ,\n"
        + "                      NUM_CTA_CONT                        ,\n"
        + "                      COD_REFERENCIA_OPE                  ,\n"
        + "                      IND_TRUNC                           ,\n"
        + "                      COD_NUM_DOC_OFICIA                  ,\n"
        + "                      COD_ESTSI                           ,\n"
        + "                      COD_PAIS                            ,\n"
        + "                      NUM_IMPOSICION                      ,\n"
        + "                      SAL_ACTUAL                          ,\n"
        + "                      TIMESTAMP_SIGLO                     ,\n"
        + "                      COD_USU_MODIF                       ,\n"
        + "                      COD_DOMINIO                         ,\n"
        + "                      NUM_NODO                            ,\n"
        + "                      IND_MOD_TABLA                       ,\n"
        + "                      NUM_MOVIMENTO                       ,\n"
        + "                      IMP_ORIGINARIO                      ,\n"
        + "                      COD_MONEDA_ORIG                     ,\n"
        + "                      IMP_CONTA                           ,\n"
        + "                      IMP_COMMISSIONI                     ,\n"
        + "                      IMP_SPESE                           ,\n"
        + "                      IMP_SPESE_RECLAM                    ,\n"
        + "                      COD_GRUPPO_SPESA                    ,\n"
        + "                      FEC_DISPONIBILITA                   ,\n"
        + "                      IND_INVIO_CONTAB                    ,\n"
        + "                      IND_MOVST                           ,\n"
        + "                      IND_MOVFOR_VAL                      ,\n"
        + "                      IND_MOVFOR_SCO                      ,\n"
        + "                      IND_MOVFOR_BLO                      ,\n"
        + "                      IND_MOVFOR_IMP                      ,\n"
        + "                      IND_MOVFOR_RIS                      ,\n"
        + "                      COD_CHECK                           ,\n"
        + "                      NUM_REL_CLIENTE                     ,\n"
        + "                      NUM_MOVST                           ,\n"
        + "                      NUM_DE_PUESTO                       ,\n"
        + "                      TIP_DISPONIBILITA                   ,\n"
        + "                      TIP_LIQUIDITA                       ,\n"
        + "                      COD_CANALE                          ,\n"
        + "                      COD_PROC_ORIG )                      \n"
        + "                VALUES (? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        CURRENT TIMESTAMP ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        CURRENT TIMESTAMP ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? ,\n"
        + "                        ? )";

    logger.debug("Performing SQL insert [ {} ]",
        "INSERT\n"
            + "                INTO STS3.T08CT006\n"
            + "                     (COD_TIP_EXPE                        ,\n"
            + "                      NUM_CTA_INT                         ,\n"
            + "                      COD_EMPRESA                         ,\n"
            + "                      FEC_OPERACION                       ,\n"
            + "                      TIMESTAMP_ALTA                      ,\n"
            + "                      COD_OPERACION                       ,\n"
            + "                      COD_REFER                           ,\n"
            + "                      DEC_REF_AMPLIADA                    ,\n"
            + "                      IMP_MOVIMIENTO                      ,\n"
            + "                      IND_NATURALEZA                      ,\n"
            + "                      FEC_CONTABLE                        ,\n"
            + "                      FEC_VALOR                           ,\n"
            + "                      SAL_DESOPER                         ,\n"
            + "                      TXT_MOVIMIENTO                      ,\n"
            + "                      NUM_OPERACION                       ,\n"
            + "                      NUM_OPER_ENLAZA                     ,\n"
            + "                      IND_OPER_ENLAZADA                   ,\n"
            + "                      IND_PTE_LIBRETA                     ,\n"
            + "                      IND_EXTRA_ENVI                      ,\n"
            + "                      NUMERO_DOCUMENTO                    ,\n"
            + "                      IND_MOVAN                           ,\n"
            + "                      IND_CONDO                           ,\n"
            + "                      IND_ORIGEN_MOV                      ,\n"
            + "                      IND_SOPORTE                         ,\n"
            + "                      COD_TIPO_INTERVEN                   ,\n"
            + "                      NOMBRE                              ,\n"
            + "                      COD_IDENTIFICA                      ,\n"
            + "                      FEC_VENCIMIENTO                     ,\n"
            + "                      COD_ENT_CP                          ,\n"
            + "                      COD_SUC_CP                          ,\n"
            + "                      COD_DIG_CONTROLCP                   ,\n"
            + "                      NUM_CUENTA_CP                       ,\n"
            + "                      FECHA_INICIO                        ,\n"
            + "                      FEC_MODIFICACION                    ,\n"
            + "                      COD_USU_ALTA                        ,\n"
            + "                      COD_CENT_ORIG                       ,\n"
            + "                      COD_DESFI                           ,\n"
            + "                      NUM_CTA_CONT                        ,\n"
            + "                      COD_REFERENCIA_OPE                  ,\n"
            + "                      IND_TRUNC                           ,\n"
            + "                      COD_NUM_DOC_OFICIA                  ,\n"
            + "                      COD_ESTSI                           ,\n"
            + "                      COD_PAIS                            ,\n"
            + "                      NUM_IMPOSICION                      ,\n"
            + "                      SAL_ACTUAL                          ,\n"
            + "                      TIMESTAMP_SIGLO                     ,\n"
            + "                      COD_USU_MODIF                       ,\n"
            + "                      COD_DOMINIO                         ,\n"
            + "                      NUM_NODO                            ,\n"
            + "                      IND_MOD_TABLA                       ,\n"
            + "                      NUM_MOVIMENTO                       ,\n"
            + "                      IMP_ORIGINARIO                      ,\n"
            + "                      COD_MONEDA_ORIG                     ,\n"
            + "                      IMP_CONTA                           ,\n"
            + "                      IMP_COMMISSIONI                     ,\n"
            + "                      IMP_SPESE                           ,\n"
            + "                      IMP_SPESE_RECLAM                    ,\n"
            + "                      COD_GRUPPO_SPESA                    ,\n"
            + "                      FEC_DISPONIBILITA                   ,\n"
            + "                      IND_INVIO_CONTAB                    ,\n"
            + "                      IND_MOVST                           ,\n"
            + "                      IND_MOVFOR_VAL                      ,\n"
            + "                      IND_MOVFOR_SCO                      ,\n"
            + "                      IND_MOVFOR_BLO                      ,\n"
            + "                      IND_MOVFOR_IMP                      ,\n"
            + "                      IND_MOVFOR_RIS                      ,\n"
            + "                      COD_CHECK                           ,\n"
            + "                      NUM_REL_CLIENTE                     ,\n"
            + "                      NUM_MOVST                           ,\n"
            + "                      NUM_DE_PUESTO                       ,\n"
            + "                      TIP_DISPONIBILITA                   ,\n"
            + "                      TIP_LIQUIDITA                       ,\n"
            + "                      COD_CANALE                          ,\n"
            + "                      COD_PROC_ORIG )                      \n"
            + "                VALUES ('" + wrapper.getCodTipExpe() + "' ,\n"
            + "                        " + wrapper.getNumCtaInt() + " ,\n"
            + "                        '" + wrapper.getCodEmpresa() + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecOperacion()) + "' ,\n"
            + "                        CURRENT TIMESTAMP                    ,\n"
            + "                        '" + wrapper.getCodOperacion() + "' ,\n"
            + "                        '" + wrapper.getCodRefer() + "' ,\n"
            + "                        '" + wrapper.getDecRefAmpliada() + "' ,\n"
            + "                        " + wrapper.getImpMovimiento() + " ,\n"
            + "                        '" + wrapper.getIndNaturaleza() + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecContable()) + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecValor()) + "' ,\n"
            + "                        " + wrapper.getSaldo() + " ,\n"
            + "                        '" + wrapper.getTxtMovimiento() + "' ,\n"
            + "                        " + wrapper.getNumOperacion() + " ,\n"
            + "                        " + wrapper.getNumOperEnlaza() + " ,\n"
            + "                        '" + wrapper.getIndOperEnlazada() + "' ,\n"
            + "                        '" + wrapper.getIndPteLibreta() + "' ,\n"
            + "                        '" + wrapper.getIndExtraEnvi() + "' ,\n"
            + "                        '" + wrapper.getNumeroDocumento() + "' ,\n"
            + "                        '" + wrapper.getIndMovan() + "' ,\n"
            + "                        '" + wrapper.getIndCondo() + "' ,\n"
            + "                        '" + wrapper.getIndOrigenMov() + "' ,\n"
            + "                        '" + wrapper.getIndSoporte() + "' ,\n"
            + "                        '" + wrapper.getCodTipoInterven() + "' ,\n"
            + "                        '" + wrapper.getNombre() + "' ,\n"
            + "                        '" + wrapper.getCodIdentifica() + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecVencimiento()) + "' ,\n"
            + "                        '" + wrapper.getCodEntCp() + "' ,\n"
            + "                        " + wrapper.getCodSucCp() + " ,\n"
            + "                        " + wrapper.getCodDigControlcp() + " ,\n"
            + "                        " + wrapper.getNumCuentaCp() + " ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFechaInicio()) + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecModificacion())
            + "' ,\n"
            + "                        '" + wrapper.getCodUsuAlta() + "' ,\n"
            + "                        '" + wrapper.getCodCentOrig() + "' ,\n"
            + "                        " + wrapper.getCodDesfi() + " ,\n"
            + "                        '" + wrapper.getNumCtaCont() + "' ,\n"
            + "                        '" + wrapper.getCodReferenciaOpe() + "' ,\n"
            + "                        '" + wrapper.getIndTrunc() + "' ,\n"
            + "                        '" + wrapper.getCodNumDocOficia() + "' ,\n"
            + "                        '" + wrapper.getCodEstsi() + "' ,\n"
            + "                        '" + wrapper.getCodPais() + "' ,\n"
            + "                        " + wrapper.getNumImposicion() + " ,\n"
            + "                        " + wrapper.getSalActual() + " ,\n"
            + "                        CURRENT TIMESTAMP             ,\n"
            + "                        '" + wrapper.getCodUsuModif() + "' ,\n"
            + "                        " + wrapper.getCodDominio() + " ,\n"
            + "                        " + wrapper.getNumNodo() + " ,\n"
            + "                        '" + wrapper.getIndModTabla() + "' ,\n"
            + "                        " + wrapper.getNumMovimento() + " ,\n"
            + "                        " + wrapper.getImpOriginario() + " ,\n"
            + "                        '" + wrapper.getCodMonedaOrig() + "' ,\n"
            + "                        " + wrapper.getImpConta() + " ,\n"
            + "                        " + wrapper.getImpCommissioni() + " ,\n"
            + "                        " + wrapper.getImpSpese() + " ,\n"
            + "                        " + wrapper.getImpSpeseReclam() + " ,\n"
            + "                        '" + wrapper.getCodGruppoSpesa() + "' ,\n"
            + "                        '" + getJdbcStringDate(wrapper.getFecDisponibilita())
            + "' ,\n"
            + "                        '" + wrapper.getIndInvioContab() + "' ,\n"
            + "                        '" + wrapper.getIndMovst() + "' ,\n"
            + "                        '" + wrapper.getIndMovforVal() + "' ,\n"
            + "                        '" + wrapper.getIndMovforSco() + "' ,\n"
            + "                        '" + wrapper.getIndMovforBlo() + "' ,\n"
            + "                        '" + wrapper.getIndMovforImp() + "' ,\n"
            + "                        '" + wrapper.getIndMovforRis() + "' ,\n"
            + "                        '" + wrapper.getCodCheck() + "' ,\n"
            + "                        " + wrapper.getNumRelCliente() + " ,\n"
            + "                        " + wrapper.getNumMovst() + " ,\n"
            + "                        " + wrapper.getNumDePuesto() + " ,\n"
            + "                        '" + wrapper.getTipDisponibilita() + "' ,\n"
            + "                        '" + wrapper.getTipLiquidita() + "' ,\n"
            + "                        '" + wrapper.getCodCanale() + "' ,\n"
            + "                        '" + wrapper.getCodProcOrig() + "')"
    );

    final PreparedStatement preparedStatement = connection.prepareStatement(sql);
    preparedStatement.setString(1, wrapper.getCodTipExpe());
    preparedStatement.setInt(2, wrapper.getNumCtaInt());
    preparedStatement.setString(3, wrapper.getCodEmpresa());
    preparedStatement.setString(4, getJdbcStringDate(wrapper.getFecOperacion()));
    preparedStatement.setString(5, wrapper.getCodOperacion());
    preparedStatement.setString(6, wrapper.getCodRefer());
    preparedStatement.setString(7, wrapper.getDecRefAmpliada());
    preparedStatement.setBigDecimal(8, wrapper.getImpMovimiento());
    preparedStatement.setString(9, wrapper.getIndNaturaleza());
    preparedStatement.setString(10, getJdbcStringDate(wrapper.getFecContable()));
    preparedStatement.setString(11, getJdbcStringDate(wrapper.getFecValor()));
    preparedStatement.setBigDecimal(12, wrapper.getSaldo());
    preparedStatement.setString(13, wrapper.getTxtMovimiento());
    preparedStatement.setLong(14, wrapper.getNumOperacion());
    preparedStatement.setLong(15, wrapper.getNumOperEnlaza());
    preparedStatement.setString(16, wrapper.getIndOperEnlazada());
    preparedStatement.setString(17, wrapper.getIndPteLibreta());
    preparedStatement.setString(18, wrapper.getIndExtraEnvi());
    preparedStatement.setString(19, wrapper.getNumeroDocumento());
    preparedStatement.setString(20, wrapper.getIndMovan());
    preparedStatement.setString(21, wrapper.getIndCondo());
    preparedStatement.setString(22, wrapper.getIndOrigenMov());
    preparedStatement.setString(23, wrapper.getIndSoporte());
    preparedStatement.setString(24, wrapper.getCodTipoInterven());
    preparedStatement.setString(25, wrapper.getNombre());
    preparedStatement.setString(26, wrapper.getCodIdentifica());
    preparedStatement.setString(27, getJdbcStringDate(wrapper.getFecVencimiento()));
    preparedStatement.setString(28, wrapper.getCodEntCp());
    preparedStatement.setInt(29, wrapper.getCodSucCp());
    preparedStatement.setInt(30, wrapper.getCodDigControlcp());
    preparedStatement.setLong(31, wrapper.getNumCuentaCp());
    preparedStatement.setString(32, getJdbcStringDate(wrapper.getFechaInicio()));
    preparedStatement.setString(33, getJdbcStringDate(wrapper.getFecModificacion()));
    preparedStatement.setString(34, wrapper.getCodUsuAlta());
    preparedStatement.setString(35, wrapper.getCodCentOrig());
    preparedStatement.setLong(36, wrapper.getCodDesfi());
    preparedStatement.setString(37, wrapper.getNumCtaCont());
    preparedStatement.setString(38, wrapper.getCodReferenciaOpe());
    preparedStatement.setString(39, wrapper.getIndTrunc());
    preparedStatement.setString(40, wrapper.getCodNumDocOficia());
    preparedStatement.setString(41, wrapper.getCodEstsi());
    preparedStatement.setString(42, wrapper.getCodPais());
    preparedStatement.setInt(43, wrapper.getNumImposicion());
    preparedStatement.setBigDecimal(44, wrapper.getSalActual());
    preparedStatement.setString(45, wrapper.getCodUsuModif());
    preparedStatement.setInt(46, wrapper.getCodDominio());
    preparedStatement.setInt(47, wrapper.getNumNodo());
    preparedStatement.setString(48, wrapper.getIndModTabla());
    preparedStatement.setInt(49, wrapper.getNumMovimento());
    preparedStatement.setBigDecimal(50, wrapper.getImpOriginario());
    preparedStatement.setString(51, wrapper.getCodMonedaOrig());
    preparedStatement.setBigDecimal(52, wrapper.getImpConta());
    preparedStatement.setBigDecimal(53, wrapper.getImpCommissioni());
    preparedStatement.setBigDecimal(54, wrapper.getImpSpese());
    preparedStatement.setBigDecimal(55, wrapper.getImpSpeseReclam());
    preparedStatement.setString(56, wrapper.getCodGruppoSpesa());
    preparedStatement.setString(57, getJdbcStringDate(wrapper.getFecDisponibilita()));
    preparedStatement.setString(58, wrapper.getIndInvioContab());
    preparedStatement.setString(59, wrapper.getIndMovst());
    preparedStatement.setString(60, wrapper.getIndMovforVal());
    preparedStatement.setString(61, wrapper.getIndMovforSco());
    preparedStatement.setString(62, wrapper.getIndMovforBlo());
    preparedStatement.setString(63, wrapper.getIndMovforImp());
    preparedStatement.setString(64, wrapper.getIndMovforRis());
    preparedStatement.setString(65, wrapper.getCodCheck());
    preparedStatement.setLong(66, wrapper.getNumRelCliente());
    preparedStatement.setInt(67, wrapper.getNumMovst());
    preparedStatement.setInt(68, wrapper.getNumDePuesto());
    preparedStatement.setString(69, wrapper.getTipDisponibilita());
    preparedStatement.setString(70, wrapper.getTipLiquidita());
    preparedStatement.setString(71, wrapper.getCodCanale());
    preparedStatement.setString(72, wrapper.getCodProcOrig());
    preparedStatement.executeUpdate();
  }

  private String getJdbcStringDate(String date) {
    if (date.contains(".")) {
      return date.substring(6, 10) + "-" + date.substring(3, 5) + "-" + date
          .substring(0, 2);
    }
    return date;
  }
}
