package com.example.sbazureappdemo.exceptions;

import org.springframework.dao.DataAccessException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import java.sql.BatchUpdateException;
import java.sql.SQLException;

public final class SqlErrorExtractor {
    private SqlErrorExtractor() {}
    public static String extractUsefulMessage(Throwable ex) {
        PSQLException pgException = findCause(ex, PSQLException.class);

        if (pgException != null) {
            ServerErrorMessage serverError = pgException.getServerErrorMessage();

            if (serverError != null) {
                String sqlState = pgException.getSQLState();
                String constraint = serverError.getConstraint();
                String detail = serverError.getDetail();
                String message = serverError.getMessage();
                String table = serverError.getTable();
                String column = serverError.getColumn();

                if ("23503".equals(sqlState)) {
                    return buildMessage(
                            "Foreign key error",
                            message,
                            detail,
                            table,
                            column,
                            constraint,
                            sqlState
                    );
                }

                if ("23505".equals(sqlState)) {
                    return buildMessage(
                            "Duplicate key error",
                            message,
                            detail,
                            table,
                            column,
                            constraint,
                            sqlState
                    );
                }

                if ("23502".equals(sqlState)) {
                    return buildMessage(
                            "Not null error",
                            message,
                            detail,
                            table,
                            column,
                            constraint,
                            sqlState
                    );
                }

                if ("42P01".equals(sqlState)) {
                    return buildMessage(
                            "Table not found error. Check if the Excel headers are being detected correctly",
                            message,
                            detail,
                            table,
                            column,
                            constraint,
                            sqlState
                    );
                }

                if ("42703".equals(sqlState)) {
                    return buildMessage(
                            "Column not found error. Check the Excel headers",
                            message,
                            detail,
                            table,
                            column,
                            constraint,
                            sqlState
                    );
                }

                return buildMessage(
                        "Database error",
                        message,
                        detail,
                        table,
                        column,
                        constraint,
                        sqlState
                );
            }

            return pgException.getMessage();
        }

        BatchUpdateException batchException = findCause(ex, BatchUpdateException.class);

        if (batchException != null && batchException.getNextException() != null) {
            return extractUsefulMessage(batchException.getNextException());
        }

        SQLException sqlException = findCause(ex, SQLException.class);

        if (sqlException != null) {
            return sqlException.getMessage();
        }

        if (ex instanceof DataAccessException dataAccessException
                && dataAccessException.getMostSpecificCause() != null) {
            return dataAccessException.getMostSpecificCause().getMessage();
        }

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            return ex.getCause().getMessage();
        }

        return ex.getMessage();
    }

    private static String buildMessage(
            String type,
            String message,
            String detail,
            String table,
            String column,
            String constraint,
            String sqlState
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append(type);

        if (message != null && !message.isBlank()) {
            sb.append(": ").append(message);
        }

        if (detail != null && !detail.isBlank()) {
            sb.append("\nDetail: ").append(detail);
        }

        if (table != null && !table.isBlank()) {
            sb.append("\nTable: ").append(table);
        }

        if (column != null && !column.isBlank()) {
            sb.append("\nColumn: ").append(column);
        }

        if (constraint != null && !constraint.isBlank()) {
            sb.append("\nConstraint: ").append(constraint);
        }

        if (sqlState != null && !sqlState.isBlank()) {
            sb.append("\nSQLState: ").append(sqlState);
        }

        return sb.toString();
    }

    private static <T extends Throwable> T findCause(Throwable ex, Class<T> clazz) {
        Throwable current = ex;

        while (current != null) {
            if (clazz.isInstance(current)) {
                return clazz.cast(current);
            }

            if (current instanceof SQLException sqlException) {
                SQLException nextException = sqlException.getNextException();

                if (nextException != null && clazz.isInstance(nextException)) {
                    return clazz.cast(nextException);
                }
            }

            current = current.getCause();
        }

        return null;
    }
}