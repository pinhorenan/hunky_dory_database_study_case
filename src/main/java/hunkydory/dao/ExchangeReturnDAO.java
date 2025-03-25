/*
 * =============================================================================
 * 10) ExchangeReturnDAO.java
 *     TABLE: troca_devolucao
 *        - id_solicitacao (PK)
 *        - data_solicitacao (DATE NOT NULL DEFAULT CURRENT_DATE)
 *        - motivo (TEXT)
 *        - status (VARCHAR(50) NOT NULL DEFAULT 'Em análise')
 *        - id_compra (FK)
 * =============================================================================
 */
package hunkydory.dao;

import hunkydory.dao.base.BaseDAO;
import hunkydory.dao.base.GenericDAO;
import hunkydory.model.ExchangeReturn;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public class ExchangeReturnDAO extends BaseDAO<ExchangeReturn> implements GenericDAO<ExchangeReturn> {

    @Override
    public boolean insert(ExchangeReturn er) {
        // We'll assume we can provide the ID manually if needed:
        String sql = "INSERT INTO troca_devolucao (id_solicitacao, data_solicitacao, motivo, status, id_compra) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, er.getRequestID());
            ps.setDate(2, er.getRequestDate() != null ? Date.valueOf(er.getRequestDate()) : null);
            ps.setString(3, er.getReason());
            ps.setString(4, er.getStatus());
            ps.setInt(5, er.getOrderID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(ExchangeReturn er) {
        String sql = "UPDATE troca_devolucao SET data_solicitacao = ?, motivo = ?, status = ?, id_compra = ? "
                + "WHERE id_solicitacao = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, er.getRequestDate() != null ? Date.valueOf(er.getRequestDate()) : null);
            ps.setString(2, er.getReason());
            ps.setString(3, er.getStatus());
            ps.setInt(4, er.getOrderID());
            ps.setInt(5, er.getRequestID());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int requestID) {
        String sql = "DELETE FROM troca_devolucao WHERE id_solicitacao = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ExchangeReturn> listAll() {
        List<ExchangeReturn> list = new ArrayList<>();
        String sql = "SELECT id_solicitacao, data_solicitacao, motivo, status, id_compra FROM troca_devolucao";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                java.sql.Date sqlDate = rs.getDate("data_solicitacao");
                java.time.LocalDate localDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;
                ExchangeReturn er = new ExchangeReturn(
                        rs.getInt("id_solicitacao"),
                        localDate,
                        rs.getInt("id_compra"),
                        rs.getString("motivo"),
                        rs.getString("status")
                );
                list.add(er);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public ExchangeReturn searchByID(int requestID) {
        String sql = "SELECT id_solicitacao, data_solicitacao, motivo, status, id_compra "
                + "FROM troca_devolucao WHERE id_solicitacao = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ExchangeReturn(
                            rs.getInt("id_solicitacao"),
                            rs.getDate("data_solicitacao")
                            rs.getString("motivo"),
                            rs.getString("status"),
                            rs.getInt("id_compra")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
