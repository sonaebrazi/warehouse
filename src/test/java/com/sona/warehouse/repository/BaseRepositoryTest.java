package com.sona.warehouse.repository;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DataMongoTest
@ExtendWith(SpringExtension.class)
abstract class BaseRepositoryTest {

    // Constant for the MongoDB port
    protected static final int MONGODB_PORT = 27017;

    // MongoDB container with a fixed port
    @Container
    private static final MongoDBContainer mongoDBContainer = createMongoDBContainer();

    @Autowired
    protected MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        clearDatabase();
    }

    private static MongoDBContainer createMongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:latest"))
                .withExposedPorts(MONGODB_PORT)
                .withCreateContainerCmdModifier(cmd -> cmd.withPortBindings(
                        new PortBinding(Ports.Binding.bindPort(MONGODB_PORT), new ExposedPort(MONGODB_PORT))
                ));
    }

    protected abstract void clearDatabase();
}
