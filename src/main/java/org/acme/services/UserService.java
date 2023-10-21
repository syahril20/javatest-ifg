package org.acme.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.acme.models.UserModels;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import java.io.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@ApplicationScoped
public class UserService {

    @Inject
    EntityManager em;


    public Response getAllUser() {
        try {
            Query query = em.createNativeQuery("select id, name, city from users");
            List<Object[]> queryResultList = query.getResultList();

            List<Map<String, Object>> resultList = new ArrayList<>();

            for (Object[] row : queryResultList) {
                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("id", row[0]);
                rowMap.put("name", row[1]);
                rowMap.put("city", row[2]);
                resultList.add(rowMap);
            }

            JsonObject result = new JsonObject();
            result.put("status", 200);
            result.put("message", "SUCCESS");
            result.put("payload", resultList);
            System.out.println(result);

            if (resultList.isEmpty()) {
                result.put("status", 200);
                result.put("message", "SUCCESS");
                result.put("payload", "KOSONG");
                return Response.ok(result).build();
            }

            return Response.ok(result).build();
        } catch (Exception e) {
            JsonObject result = new JsonObject();
            result.put("status", 400);
            result.put("message", "FAILED");
            result.put("payload", e.getMessage());
            return Response.ok(result).build();
        }
    }

    @Transactional
    public Response addUser(Object param) {
        try {
            UserModels user = new UserModels();
            ObjectMapper om = new ObjectMapper();
            Map<String, Object> req = om.convertValue(param, Map.class);
            user.name = (String) req.get("name");
            user.city = (String) req.get("city");
            user.persist();

            JsonObject result = new JsonObject();
            result.put("status", 200);
            result.put("message", "SUCCESS");
            result.put("payload", user);
            return Response.ok(result).build();

        } catch (Exception e) {
            JsonObject result = new JsonObject();
            result.put("status", 400);
            result.put("message", "FAILED");
            result.put("payload", e.getMessage());
            return Response.ok(result).build();
        }
    }

    public static String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

    @Transactional
    public Response uploadFile(MultipartFormDataInput form){
        final String name = "name";
        final String city = "city";
        try{
            Map<String, List<InputPart>> uploadForm = form.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");
            InputPart inputPart = inputParts.get(0);
            MultivaluedMap<String, String> header = inputPart.getHeaders();
            String fileName = getFileName(header);
            InputStream inputStream = inputPart.getBody(InputStream.class,null);

            int fileIndex = fileName.lastIndexOf(".");
            if(!Pattern.compile("(?:xlsx|xls)").matcher(fileName.substring(fileIndex+1).toLowerCase()).find()){
                return Response.ok("FAILED").build();
            }

            XSSFSheet sheet = new XSSFWorkbook(inputStream).getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            List<String> headers = new ArrayList<>();
            if(rowIterator.hasNext()){
                Row row = rowIterator.next();
                for(Cell cell : row){
                    headers.add(cell.getStringCellValue());
                }
            }
            List<UserModels> listData = new ArrayList<>();
            while (rowIterator.hasNext()){
                Row row = rowIterator.next();

                UserModels data = new UserModels();

                data.name = row.getCell(headers.indexOf(name)).getStringCellValue();
                data.city = row.getCell(headers.indexOf(city)).getStringCellValue();

                listData.add(data);
            }
            UserModels.persist(listData);
            JsonObject result = new JsonObject();
            result.put("status", 200);
            result.put("message", "SUCCESS");
            result.put("payload", listData);
            return Response.ok(result).build();

        }
        catch (Exception e){
            return Response.ok(e.getMessage()).build();
        }
    }

    public Response getUser() {
        try {
            Query query = em.createNativeQuery("select id, name, city from users");
            List<Object[]> queryResultList = query.getResultList();

            List<Map<String, Object>> resultList = new ArrayList<>();

            for (Object[] row : queryResultList) {
                Map<String, Object> rowMap = new HashMap<>();
                rowMap.put("id", row[0]);
                rowMap.put("name", row[1]);
                rowMap.put("city", row[2]);
                resultList.add(rowMap);
            }

            List<String> headers = Arrays.asList("id",
                    "name",
                    "city");

            StringBuilder sbHead = new StringBuilder();
            for (int i = 0; i < headers.size(); i++) {
                sbHead.append(headers.get(i));
                sbHead.append(i == headers.size() - 1 ? "\n" : ",");
            }

            StreamingOutput stream = new StreamingOutput() {
                @Override
                public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                    Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    writer.write(sbHead.toString());
                    for (Map<String, Object> d : resultList) {
                        StringBuilder sbBody = new StringBuilder();
                        for (int i = 0; i < headers.size(); i++) {
                            sbBody.append("\"").append(d.get(headers.get(i))).append("\"");
                            sbBody.append(i == headers.size() - 1 ? "\n" : ",");
                        }
                        writer.write(sbBody.toString());
                    }
                    writer.flush();
                }
            };

            return Response.ok(stream).header("Content-Disposition", "attachment;filename=getUser.csv").build();
        } catch (Exception e) {
            return Response.status(400).build();
        }
    }

}
