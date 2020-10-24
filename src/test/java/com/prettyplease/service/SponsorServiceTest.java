package com.prettyplease.service;

import com.prettyplease.model.tables.Sponsor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SponsorServiceTest {

    @Mock
    private Connection mockConnection;

    @Mock
    PreparedStatement mockPreparedStatement;

    @Mock
    ResultSet mockResultSet;

    @InjectMocks
    private SponsorServiceImpl sponsorService;


    @BeforeAll
    public static void setUp() {
        Mockito.mockStatic(DriverManager.class);
    }

    @Test
    void testSuccessfullyReturnsList() throws SQLException, ClassNotFoundException {
        Mockito.when(DriverManager.getConnection(Mockito.anyString())).thenReturn(mockConnection);
        Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(mockResultSet.getString("sponsorId")).thenReturn("TESTID");

        List<Sponsor> sponsors = sponsorService.getSponsors("SPON001");
        assertEquals(1, sponsors.size());
    }

    @Test
    void testSuccessfullyReturnsListWithData() throws SQLException, ClassNotFoundException {
        Mockito.when(DriverManager.getConnection(Mockito.anyString())).thenReturn(mockConnection);
        Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        Mockito.when(mockResultSet.getString("sponsorId")).thenReturn("TEST VALUE");

        List<Sponsor> sponsors = sponsorService.getSponsors("SPON001");
        assertEquals("TEST VALUE", sponsors.get(0).getSponsorId());
    }

    @Test
    void testSuccessfullyReturnsEmptyListWithNoData() throws SQLException, ClassNotFoundException {
        Mockito.when(DriverManager.getConnection(Mockito.anyString())).thenReturn(mockConnection);
        Mockito.when(mockConnection.prepareStatement(Mockito.anyString())).thenReturn(mockPreparedStatement);
        Mockito.when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(false);

        List<Sponsor> sponsors = sponsorService.getSponsors("SPON001");
        assertEquals(0, sponsors.size());
    }


}