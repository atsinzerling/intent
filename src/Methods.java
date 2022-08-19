import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Methods {
    public static String wrap(String orig) {

        char[] charArr = (orig + " ").toCharArray();

        int lastLineBreak = 0;
        int lastSpace = 0;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == ' ') {
                if (i - lastLineBreak >= 60) {
                    charArr[lastSpace] = '\n';
                    lastLineBreak = lastSpace;
                }
                lastSpace = i;
            }
        }
        return new String(charArr).substring(0, charArr.length - 1);
    }

    public static void errorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean isAdmin(String username) {
        return (username.length() > 1 && username.substring(username.length() - 2).equals("/A")) ||
            username.equals("admin");
    }

    public static void updateUserLog(String user, String log) {

        java.sql.Date date =
            java.sql.Date.valueOf(new Timestamp(System.currentTimeMillis()).toLocalDateTime().toLocalDate());

        try {
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();


            String selectsql =
                "SELECT * from " + MainPage.schema + ".useractions WHERE (User, Date) = ('" + user + "','" + date +
                    "')";
            ResultSet rs = stmt.executeQuery(selectsql);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            String dateeime = sdf.format(new Timestamp(System.currentTimeMillis()));

            if (rs.next()) {
                System.out.println(rs.getString("User") + " " + rs.getDate("Date") + " " + rs.getString("Log"));
                String updsql =
                    "UPDATE " + MainPage.schema + ".useractions SET Log='" + dateeime + " " + log + ";<<<:::===" +
                        rs.getString("Log") + "' WHERE (User, Date) = ('" + user + "','" + date + "')";
                System.out.println(stmt.executeUpdate(updsql));

            } else {
                System.out.println("no record");
                String sqll =
                    "INSERT INTO " + MainPage.schema + ".useractions " + "VALUES ('" + user + "','" + date + "','" +
                        dateeime + " " + log + "')";
                System.out.println(stmt.executeUpdate(sqll));
            }
//            System.out.println(stmt.executeUpdate(sqll));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void export_func(String filepath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet spreadsheet = workbook.createSheet("Intent Data");
        XSSFRow row = spreadsheet.createRow(0);

        try {
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM " + MainPage.schema + ".items" + "\n" +
                "ORDER BY CASE WHEN DateModified IS NULL THEN DateTime ELSE DateModified END DESC");
            ResultSetMetaData metadata = rs.getMetaData();

            int columnCount = metadata.getColumnCount();

            System.out.print("test_table columns : ");

            for (int i = 0; i < columnCount; i++) {

                String columnName = metadata.getColumnName(i + 1);
                switch (columnName) {
                case "DateTime":
                    columnName = "Date Created";
                    break;
                case "DateModified":
                    columnName = "Date Modified";
                    break;
                case "OtherRecords":
                    columnName = "History";
                    break;
                case "POnumber":
                    columnName = "PO#";
                    break;
                }
                System.out.print(columnName + " ");

                row.createCell(i).setCellValue(columnName);
            }
            System.out.println("");

            int rowCounter = 1;

            while (rs.next()) {
                row = spreadsheet.createRow(rowCounter);
                for (int i = 0; i < columnCount; i++) {
                    switch (i) {
                    case 0:
                        row.createCell(i).setCellValue(rs.getLong(0 + 1));
                        break;
                    case 8:

                    case 11:
                        Cell timecell = row.createCell(i);
                        CellStyle timestyle = workbook.createCellStyle();
                        timestyle.setDataFormat(
                            workbook.getCreationHelper().createDataFormat().getFormat("d/m/yy h:mm"));
                        timecell.setCellStyle(timestyle);

                        if (rs.getTimestamp(i + 1) != null) {
                            timecell.setCellValue(new Date(rs.getTimestamp(i + 1).getTime()));
                        }
                        break;
                    default:
                        row.createCell(i).setCellValue(rs.getString(i + 1));
                        break;
                    }
                }
                rowCounter++;
            }
            FileOutputStream out = new FileOutputStream(filepath);

            workbook.write(out);
            out.close();
            workbook.close();

            stmt.close();
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            Platform.runLater(() -> {

                MainPage.databaseErrorAlert(ex).showAndWait();
            });
            ex.printStackTrace();
        } catch (IOException iox) {
            Platform.runLater(() -> {

                MainPage.ioErrorAlert(iox).showAndWait();
            });
            iox.printStackTrace();
        }
    }

    public static void copyAct(ArrayList<Item> itemsSelect) {

        if (itemsSelect != null && itemsSelect.size() > 0) {
            StringBuilder clipboardString = new StringBuilder();

            for (Item it : itemsSelect) {
                clipboardString.append(it.excelFormat());
            }
            final ClipboardContent content = new ClipboardContent();

            try {
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);
                System.out.println("text copied:\n" + clipboardString);
            } catch (Exception e) {
                System.out.println("copy failed");
                e.printStackTrace();
            }
        } else {
            System.out.println("nothing to copy");
        }
    }

    public static long generateCustomSKU() {
        long currSKU = 0;
        try {
            Connection conn = DriverManager.getConnection(MainPage.urll, MainPage.user, MainPage.passw);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SKUcustom from " + MainPage.schema + ".addit_data");
            rs.next();
            long lastSKU = rs.getLong(1); //last used sku
            currSKU = lastSKU++;
            rs.close();
            while (stmt.executeQuery("SELECT * from " + MainPage.schema + ".items WHERE SKU=" + currSKU)
                .next()) { //while used    is it used?
                currSKU++;
            }
            //we got the first SKU thats not used
            if (currSKU > lastSKU + 1) {
                stmt.executeUpdate(
                    "UPDATE " + MainPage.schema + ".addit_data SET SKUcustom=" + (currSKU - 1) + " WHERE IDcolumn=1");
            }
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
//            MainPage.databaseErrorAlert(ex).showAndWait();
        }
        return currSKU;
    }

    //    public static boolean isChildFocused(javafx.scene.Parent parent)
//    {
//        for (Node node : parent.getChildrenUnmodifiable())
//        {
//            if (node.isFocused())
//            {
//                return true;
//            }
//            else if (node instanceof javafx.scene.Parent)
//            {
//                if (isChildFocused((javafx.scene.Parent)node))
//                {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
    public static void boostScroll(ScrollPane scrollPane, Node content, double origBoost) {
        scrollPane.vvalueProperty().addListener(
            (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                //System.out.println("from "+oldValue+" to "+newValue);
                double boost =
                    origBoost / (content instanceof VBox ? ((VBox) content).getHeight() : ((HBox) content).getHeight());
                double diff = (double) newValue - (double) oldValue;
                if (almostEqual(diff, boost, 0.00001) || almostEqual(diff, (0 - boost), 0.00001) ||
                    almostEqual(diff, 0, 0.000001)) {
                    return;
                }
                double newww = scrollPane.getVvalue() + (diff > 0 ? boost : (0 - boost));
                //System.out.println("\tnewww val - "+newww+"; diff - "+diff);
                if (0 < newww && newww < 1) {
                    //System.out.println("\tsetting value");
                    scrollPane.setVvalue(newww);
                }
            });
    }

    public static boolean almostEqual(double a, double b, double eps) {
        return Math.abs(a - b) < eps;
    }

}

