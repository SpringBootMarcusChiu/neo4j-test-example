package com.neo4j.example.springdataneo4jintroapp.service;

import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PersisterService {

    @Autowired
    private SessionFactory sessionFactory;

    public void test() {
        String query = "CALL apoc.load.csv('http://localhost:8080/file', {nullValues:['null']})\n" +
                "YIELD map WITH map\n" +
                "MERGE (n:User {name: map.firstName}) SET n += map";
        Result r = sessionFactory.openSession().query(query, new HashMap<>());
    }
}
