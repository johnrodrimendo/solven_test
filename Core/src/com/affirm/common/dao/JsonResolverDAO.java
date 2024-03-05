/**
 *
 */
package com.affirm.common.dao;

import com.affirm.common.model.transactional.MatiResult;
import com.affirm.common.util.GzipUtil;
import com.affirm.common.util.SqlErrorMessageException;
import com.affirm.common.util.SqlErrorQueryException;
import com.affirm.system.configuration.Configuration;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameterValue;

import javax.annotation.Resource;
import java.io.*;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author jrodriguez
 * <p>
 * Use it for the DAO that process the json format results from BD.<br>
 * The child DAO should never return JSON, instead the class that it
 * represents.
 */
public abstract class JsonResolverDAO {

    @Resource(name = "readableJdbcTemplate")
    private JdbcTemplate readableJdbcTemplate;

    @Resource(name = "trxJdbcTemplate")
    private JdbcTemplate trxJdbcTemplate;

    @Resource(name = "evaluationJdbcTemplate")
    private JdbcTemplate evaluationJdbcTemplate;

    @Resource(name = "externalJdbcTemplate")
    private JdbcTemplate externalJdbcTemplate;

    @Resource(name = "interactionJdbcTemplate")
    private JdbcTemplate interactionJdbcTemplate;

    @Resource(name = "repositoryJdbcTemplate")
    private JdbcTemplate repositoryJdbcTemplate;

    @Resource(name = "evaluationFollowerJdbcTemplate")
    private JdbcTemplate evaluationFollowerJdbcTemplate;

    private static final Logger logger = Logger.getLogger(JsonResolverDAO.class);

    public static final int TRX_DB = 1;
    public static final int READABLE_DB = 2;
    public static final int REPOSITORY_DB = 3;
    public static final int EVALUATION_DB = 4;
    public static final int EXTERNAL_DB = 5;
    public static final int INTERACTION_DB = 6;
    public static final int EVALUATION_FOLLOWER_DB = 7;

    /**
     * Method for the execution of the custom queryForObjectTrx method with logging enabled
     *
     * @param query
     */
    public <T> T queryForObjectTrx(String query, Class<T> returningObject, SqlParameterValue... params) {
        return queryForObjectTrx(query, returningObject, true, params);
    }

    public <T> T queryForObjectTrx(String query, Class<T> returningObject, boolean logging, SqlParameterValue... params) {
        return queryForObject(query, returningObject, logging, TRX_DB, params);
    }


    public <T> T queryForObject(String query, Class<T> returningObject, int database, SqlParameterValue... params) {
        return queryForObject(query, returningObject, true, database, params);
    }

    public <T> T queryForObjectEvaluation(String query, Class<T> returningObject, SqlParameterValue... params) {
        return queryForObject(query, returningObject, true, EVALUATION_DB, params);
    }

    public <T> T queryForObjectExternal(String query, Class<T> returningObject, SqlParameterValue... params) {
        return queryForObject(query, returningObject, true, EXTERNAL_DB, params);
    }

    public <T> T queryForObjectInteraction(String query, Class<T> returningObject, SqlParameterValue... params) {
        return queryForObject(query, returningObject, true, INTERACTION_DB, params);
    }

    public <T> T queryForObjectEvaluationFollower(String query, Class<T> returningObject, SqlParameterValue... params) {
        return queryForObject(query, returningObject, true, EVALUATION_FOLLOWER_DB, params);
    }

    /**
     * Generic method for the execution of any query with result.
     * It accepts JSONObject and JSONArray as returningObject
     * If it was about to return null, it will instead return the defaultValue
     *
     * @param query
     */

    public <T> T queryForObject(String query, Class<T> returningObject, T defaultValue, int database, SqlParameterValue... params) {
        T t = queryForObject(query, returningObject, database, params);
        return t == null ? defaultValue : t;
    }

    /**
     * Generic method for the execution of any query with result.
     * It accepts JSONObject and JSONArray as returningObject
     *
     * @param query
     */
    public <T> T queryForObject(String query, Class<T> returningObject, boolean logging, int database, SqlParameterValue... params) {
        return queryForObject(query, returningObject, logging, database, 0, params);
    }

    private <T> T queryForObject(String query, Class<T> returningObject, boolean logging, int database, int retries, SqlParameterValue... params) {
        long startTime = new Date().getTime();
        try {
            T result = null;
            JdbcTemplate datasource;

            if (database == READABLE_DB && readableJdbcTemplate != null) {
                datasource = readableJdbcTemplate;
            } else if (database == REPOSITORY_DB) {
                datasource = repositoryJdbcTemplate;
            } else if (database == EVALUATION_DB) {
                datasource = evaluationJdbcTemplate;
            } else if (database == EXTERNAL_DB) {
                datasource = externalJdbcTemplate;
            } else if (database == INTERACTION_DB) {
                datasource = interactionJdbcTemplate;
            } else if (database == EVALUATION_FOLLOWER_DB) {
                if(evaluationFollowerJdbcTemplate != null)
                    datasource = evaluationFollowerJdbcTemplate;
                else
                    datasource = evaluationJdbcTemplate;
            } else {
                datasource = trxJdbcTemplate;
            }

            if (returningObject.isAssignableFrom(JSONObject.class) || returningObject.isAssignableFrom(JSONArray.class)) {
                String resultString = datasource.queryForObject(query, String.class, params);
                if (logging) {
                    long resultTime = new Date().getTime() - startTime;
                    logger.debug("Result from query \n\t-> " + recreateQueryLog(query, params) + "\n\t<- " + (!Configuration.hostEnvIsProduction() ? resultString : GzipUtil.encode(GzipUtil.zip(resultString))) + "\n\t[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : ""));
                }

                if (resultString != null && !resultString.isEmpty()) {
                    result = returningObject.isAssignableFrom(JSONObject.class) ? (T) new JSONObject(resultString) : (T) new JSONArray(resultString);
                }
            } else {
                result = datasource.queryForObject(query, returningObject, params);
                if (logging) {
                    long resultTime = new Date().getTime() - startTime;
                    logger.debug("Result from query \n\t-> " + recreateQueryLog(query, params) + "\n\t<- " + (!Configuration.hostEnvIsProduction() ? result : GzipUtil.encode(GzipUtil.zip(String.valueOf(result)))) + "\n\t[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : ""));
                }
            }
            return result;
        } catch (EmptyResultDataAccessException sqlException) {
            if (logging) {
                long resultTime = new Date().getTime() - startTime;
                logger.debug("Result from query \n\t-> " + recreateQueryLog(query, params) + "\n\t<- " + null + "\n\t[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : ""));
            }
            return null;
        } catch (DataAccessException sqlException) {
            long resultTime = new Date().getTime() - startTime;
            String parameters = this.recreateQueryLog(query, params) + "[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : "") + "\n\t";
            String errorMsg = captureErrorMessage(sqlException.getMostSpecificCause());
            if (errorMsg != null) {
                logger.error("Error message not detected: " + sqlException.getMessage());
                throw new SqlErrorMessageException(errorMsg, null);
            } else {
                errorMsg = sqlException.getMessage();

                // If is an error of serialization, repeat until it orks. Max 5 retries
                if ((errorMsg.toLowerCase().contains("could not serialize access due to concurrent update".toLowerCase())
                        || errorMsg.toLowerCase().contains("deadlock detected".toLowerCase()))
                        && retries < 5) {
                    return queryForObject(query, returningObject, logging, database, retries + 1, params);
                } else {
                    throw new SqlErrorQueryException(parameters.concat(errorMsg), null);
                }
            }
        } catch (Exception exception) {
            long resultTime = new Date().getTime() - startTime;
            String parameters = this.recreateQueryLog(query, params) + "[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : "") + "\n\t";
            String errorMsg = exception.getMessage();

            // If is an error of serialization, repeat until it orks. Max 5 retries
            if ((errorMsg.toLowerCase().contains("could not serialize access due to concurrent update".toLowerCase())
                    || errorMsg.toLowerCase().contains("deadlock detected".toLowerCase()))
                    && retries < 5) {
                return queryForObject(query, returningObject, logging, database, retries + 1, params);
            } else {
                throw new SqlErrorQueryException(parameters.concat(errorMsg), null);
            }
        }
    }

    /**
     * Generic method for the execution of any query without result
     *
     * @param query
     */
    public void updateTrx(String query, SqlParameterValue... params) {
        update(query, TRX_DB, params);
    }

    public void update(String query, int database, SqlParameterValue... params) {
        update(query, database, 0, params);
    }

    public void update(String query, int database, int retries, SqlParameterValue... params) {
        long startTime = new Date().getTime();
        try {

            switch (database) {
                case EVALUATION_DB:
                    evaluationJdbcTemplate.update(query, params);
                    break;
                case EXTERNAL_DB:
                    externalJdbcTemplate.update(query, params);
                    break;
                case INTERACTION_DB:
                    interactionJdbcTemplate.update(query, params);
                    break;
                case REPOSITORY_DB:
                    repositoryJdbcTemplate.update(query,params);
                    break;
                default:
                    trxJdbcTemplate.update(query, params);
                    break;
            }

            long resultTime = new Date().getTime() - startTime;

            logger.debug("Result from query \n\t-> " + recreateQueryLog(query, params) + "\n\t<- " + "[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : ""));

        } catch (DataAccessException sqlException) {
            long resultTime = new Date().getTime() - startTime;
            String parameters = null;
            if (params instanceof SqlParameterValue[]) {
                parameters = this.recreateQueryLog(query, params) + "[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : "") + "\n\t";
            }

            String errorMsg = captureErrorMessage(sqlException.getMostSpecificCause());
            if (errorMsg != null) {
                throw new SqlErrorMessageException(errorMsg, null);
            } else {
                errorMsg = sqlException.getMessage();

                // If is an error of serialization, repeat until it orks. Max 5 retries
                if ((errorMsg.toLowerCase().contains("could not serialize access due to concurrent update".toLowerCase())
                        || errorMsg.toLowerCase().contains("deadlock detected".toLowerCase()))
                        && retries < 5) {
                    update(query, database, retries + 1, params);
                } else {
                    throw new SqlErrorQueryException(parameters != null ? parameters.concat(errorMsg) : errorMsg, null);
                }
            }
        } catch (Exception exception) {
            long resultTime = new Date().getTime() - startTime;
            String parameters = null;
            if (params instanceof SqlParameterValue[]) {
                parameters = this.recreateQueryLog(query, params) + "[" + resultTime + " ms]" + (retries > 0 ? "[" + retries + " retries]" : "") + "\n\t";
            }

            String errorMsg = exception.getMessage();


            // If is an error of serialization, repeat until it orks. Max 5 retries
            if ((errorMsg.toLowerCase().contains("could not serialize access due to concurrent update".toLowerCase())
                    || errorMsg.toLowerCase().contains("deadlock detected".toLowerCase()))
                    && retries < 5) {
                update(query, database, retries + 1, params);
            } else {
                throw new SqlErrorQueryException(parameters != null ? parameters.concat(errorMsg) : errorMsg, null);
            }

        }
    }

    private String captureErrorMessage(Throwable error) {
        if (error.getMessage() != null && error.getMessage().contains("{") && error.getMessage().contains("}")) {
            String nestedErrorMsg = error.getMessage();
            return nestedErrorMsg.substring(nestedErrorMsg.lastIndexOf("{") + 1, nestedErrorMsg.lastIndexOf("}"));
        } else {
            return null;
        }
    }

    private String recreateQueryLog(String query, Object... params) {
        try {
            String replacedQuery = query;
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    replacedQuery = replacedQuery.replaceFirst("\\?", (params[i] instanceof SqlParameterValue ? ((SqlParameterValue) params[i]).getValue() + "" : params[i] + "").replaceAll("\\$", "\\\\\\$"));
                }
            }
            return replacedQuery;
        } catch (Exception ex) {
            logger.error("Error replacing query", ex);
            return query;
        }
    }

    public long copyFromCommandToTempTable(File file, Integer database, String table) throws SQLException, IOException {
        return copyFromCommandToTempTable(file,database,table,false, null);
    }

    public long copyFromCommandToTempTable(File file, Integer database, String table, String additionalCommand) throws SQLException, IOException {
        return copyFromCommandToTempTable(file,database,table,false, additionalCommand);
    }

    public long copyFromCommandToTempTable(File file, Integer database, String table,boolean truncateTable) throws SQLException, IOException {
        return copyFromCommandToTempTable(file,database,table, truncateTable, null);
    }

    public long copyFromCommandToTempTable(File file, Integer database, String table, boolean truncateTable, String additionalCommand) throws SQLException, IOException {
        JdbcTemplate datasource;
        if (database == READABLE_DB && readableJdbcTemplate != null) {
            datasource = readableJdbcTemplate;
        } else if (database == REPOSITORY_DB) {
            datasource = repositoryJdbcTemplate;
        } else if (database == EVALUATION_DB) {
            datasource = evaluationJdbcTemplate;
        } else if (database == EXTERNAL_DB) {
            datasource = externalJdbcTemplate;
        } else if (database == INTERACTION_DB) {
            datasource = interactionJdbcTemplate;
        } else if (database == EVALUATION_FOLLOWER_DB) {
            if(evaluationFollowerJdbcTemplate != null)
                datasource = evaluationFollowerJdbcTemplate;
            else
                datasource = evaluationJdbcTemplate;
        } else {
            datasource = trxJdbcTemplate;
        }

        if(truncateTable) truncateTable( database, table);
        long rowsInserted = new CopyManager(datasource.getDataSource().getConnection().unwrap(BaseConnection.class))
                .copyIn(
                        additionalCommand == null ?
                        "COPY "+table+" FROM STDIN ( FORMAT csv, HEADER)" :
                                "COPY "+table+" FROM STDIN ("+additionalCommand+", FORMAT csv, HEADER)"
                        ,
                        new BufferedReader(new FileReader(file))
                );
        return rowsInserted;
    }

    private void truncateTable(Integer database, String table){
        update("TRUNCATE "+table, database);
    }
}