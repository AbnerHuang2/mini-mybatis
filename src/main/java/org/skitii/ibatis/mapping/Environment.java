package org.skitii.ibatis.mapping;

import lombok.Data;
import org.skitii.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;

/**
 * @author skitii
 * @since 2023/11/09
 **/
@Data
public class Environment {
    private final String id;
    private final TransactionFactory transactionFactory;
    private final DataSource dataSource;


    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }

    public static class Builder {
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id) {
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory) {
            this.transactionFactory = transactionFactory;
            return this;
        }

        public Builder dataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public String id() {
            return this.id;
        }

        public Environment build() {
            return new Environment(this.id, this.transactionFactory, this.dataSource);
        }
    }


}
