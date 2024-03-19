package com.simulation.fifa.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.P6SpyOptions;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import jakarta.annotation.PostConstruct;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Stack;
import java.util.function.Predicate;

@Configuration
public class P6SpySqlFormatterConfig implements MessageFormattingStrategy {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String P6SPY_FORMATTER = "MyP6spyFormattingStrategy";

    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(this.getClass().getName());
    }

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        // 호출 스택
        StringBuilder sb = new StringBuilder();
        sb.append(getStackBuilder())
                .append(NEW_LINE)
                .append(NEW_LINE)
                .append("\t").append(String.format("Connection ID: %s", connectionId))
                .append(NEW_LINE)
                .append("\t").append(String.format("Execution Time: %s ms", elapsed))
                .append(NEW_LINE);

        // 쿼리
        if (StringUtils.hasText(sql)) {
            sb.append(FormatStyle.HIGHLIGHT.getFormatter().format(formatSql(category, sql)));
        }

        return sb.append(NEW_LINE).toString();
    }


    private StringBuilder getStackBuilder() {
        final Stack<String> callStack = new Stack<>();
        Arrays.stream(new Throwable().getStackTrace())
                .map(StackTraceElement::toString)
                .filter(isExcludeWords())
                .forEach(callStack::push);

        final StringBuilder callStackBuilder = new StringBuilder();
        while (!callStack.empty()) {
            callStackBuilder.append(MessageFormat.format("{0}{1}", NEW_LINE, callStack.pop()));
        }
        return callStackBuilder;
    }

    private Predicate<String> isExcludeWords() {
        return charSequence -> charSequence.contains("Service.") && !charSequence.contains(P6SPY_FORMATTER);
    }

    private String formatSql(String category, String sql) {
        if (sql != null && !sql.trim().isEmpty() && Category.STATEMENT.getName().equals(category)) {
            String trimmedSQL = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSQL.startsWith("create") || trimmedSQL.startsWith("alter") || trimmedSQL.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            } else {
                sql = FormatStyle.BASIC.getFormatter().format(sql);
            }
            return sql;
        }

        return sql;
    }
}