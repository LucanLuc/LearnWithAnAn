package repository;

import java.sql.Connection;
import java.util.function.Supplier;

public class ReviewSubmissionRepository {

    private final Supplier<Connection> connectionProvider;

    public ReviewSubmissionRepository() {
        this(DatabaseConnection::getConnection);
    }

    public ReviewSubmissionRepository(Supplier<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    // Keep your saveReviewAndUpdateStats() method here.
}