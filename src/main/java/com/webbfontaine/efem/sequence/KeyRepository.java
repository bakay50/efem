/*
 * Copyrights 2002-2014 Webb Fontaine
 * Developer: Sargis Harutyunyan
 * Date: 04 avr. 2014
 * This software is the proprietary information of Webb Fontaine.
 * Its use is subject to License terms.
 */
package com.webbfontaine.efem.sequence;

import groovy.sql.Sql;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("CheckStyle")
@Repository
public class KeyRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyRepository.class);

    private JdbcTemplate jdbcTemplate;

    public long findLatestTrNumber(int year,String requestType) {
        Date yearStart = new Date(new LocalDateTime(year, 1, 1, 0, 0, 0, 0).toDate().getTime());
        LOGGER.info("Using request date after or equal {}", yearStart);
        String seqQuery = "SELECT MAX(REQ_SEQ) FROM EXCHANGE WHERE REQUEST_DATE >= ? and REQ_YER = ? and REQUEST_TYPE = ? GROUP BY REQUEST_TYPE";

        Long result = queryForSequenceNumber(seqQuery, new Object[]{yearStart, year, requestType});

        LOGGER.debug("Latest Tr Number for year {} is {} with request type {} ", year, result,requestType);
        return result;
    }

    public long findLatestRegNumber(int year,String bankCode) {
        Date yearStart = new Date(new LocalDateTime(year, 1, 1, 0, 0, 0, 0).toDate().getTime());
        LOGGER.info("Using request date after or equal {}", yearStart);
        String seqQuery = "SELECT MAX(REG_SEQ) FROM EXCHANGE WHERE REQUEST_DATE >= ? and REQ_YER = ? and BANK_CODE = ? GROUP BY BANK_CODE";

        Long result = queryForSequenceNumber(seqQuery, new Object[]{yearStart, year, bankCode});

        LOGGER.debug("Latest Reg Number for year {} is {} with Bank Code {} ", year, result, bankCode);
        return result;
    }

    public long findLastedCurrencyTransferRegNumber(int year) {
        Date yearStart = new Date(new LocalDateTime(year, 1, 1, 0, 0, 0, 0).toDate().getTime());
        LOGGER.info("Using request date after or equal {}", yearStart);
        String sequenceQuery = "SELECT MAX(REQ_SEQ) from CURRENCY_TRANSFER WHERE REQUEST_DATE >= ? AND REQ_YER = ?";
        Long result = queryForSequenceNumber(sequenceQuery, new Object[]{yearStart, year});
        LOGGER.debug("Latest REG Number for year {} is {}", year, result);
        return result;
    }

    public long findLatestRepatNumber(int year) {
        Date yearStart = new Date(new LocalDateTime(year, 1, 1, 0, 0, 0, 0).toDate().getTime());
        LOGGER.info("Using request date after or equal {}", yearStart);
        String sequenceQuery = "SELECT MAX(REQ_SEQ) from REPATRIATION WHERE REQUEST_DATE >= ? AND REQ_YER = ?";
        Long result = queryForSequenceNumber(sequenceQuery, new Object[]{yearStart, year});
        LOGGER.debug("Latest REG Number for year {} is {}", year, result);
        return result;
    }

    public long findLastedOrderTransferRegNumber(int year) {
        Date yearStart = new Date(new LocalDateTime(year, 1, 1, 0, 0, 0, 0).toDate().getTime());
        LOGGER.info("Using request date after or equal {}", yearStart);
        String sequenceQuery = "SELECT MAX(REQ_SEQ) from TRANSFER_ORDER WHERE REQUEST_DATE >= ? AND REQ_YER = ?";
        Long result = queryForSequenceNumber(sequenceQuery, new Object[]{yearStart, year});
        LOGGER.debug("Latest REG Number for year {} is {}", year, result);
        return result;
    }

    @Autowired
    public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private Long queryForSequenceNumber(String sequenceScript, Object[] queryParams){
        Long result = jdbcTemplate.query(sequenceScript, queryParams, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                long max = 0;
                if (rs.next()) {
                    max = rs.getLong(1);
                }
                return max;
            }
        });
        return result;
    }

}
