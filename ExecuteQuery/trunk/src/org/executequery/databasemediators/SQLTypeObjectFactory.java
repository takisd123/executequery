package org.executequery.databasemediators;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.underworldlabs.jdbc.DataSourceException;

public class SQLTypeObjectFactory {

    public Object create(int type, Object value) {
        
        Object valueAsType = value;
        String valueAsString = value.toString();
        switch (type) {
        
            case Types.TINYINT:
                valueAsType = Byte.valueOf(valueAsString);
                break;
        
            case Types.BIGINT:
                valueAsType = Long.valueOf(valueAsString);
                break;

            case Types.SMALLINT:
                valueAsType = Short.valueOf(valueAsString);
                break;
        
            case Types.LONGVARCHAR:
            case Types.CHAR:
            case Types.VARCHAR:
                valueAsType = valueAsString;
                break;
        
            case Types.BIT:
            case Types.BOOLEAN:
                valueAsType = Boolean.valueOf(valueAsString);
                break;
        
            case Types.NUMERIC:
            case Types.DECIMAL:
                valueAsType = new BigDecimal(valueAsString);
                break;
                
            case Types.INTEGER:
                valueAsType = Integer.valueOf(valueAsString);
                break;
                
            case Types.REAL:
                valueAsType = Float.valueOf(valueAsString);
                break;
        
            case Types.FLOAT:
            case Types.DOUBLE:
                valueAsType = Double.valueOf(valueAsString);
                break;

            case Types.DATE:
                valueAsType = stringAsDate(valueAsString);
                break;

            case Types.TIME:
                valueAsType = stringAsTime(valueAsString);
                break;
                
            case Types.TIMESTAMP:
                valueAsType = stringAsTimestamp(valueAsString);
                break;

        }

        return valueAsType;
    }
    
    private Date stringAsDate(String value) {
        
        try {
        
            return new java.sql.Date(parseDate(value).getTime());

        } catch (ParseException e) {
            
            throw new DataSourceException(e);
        }

    }
    
    private Timestamp stringAsTimestamp(String value) {
        
        try {
            
            return new Timestamp(parseDateTime(value).getTime());
            
        } catch (ParseException e) {
            
            throw new DataSourceException(e);
        }
        
    }

    private Time stringAsTime(String value) {
        
        try {
            
            return new Time(new SimpleDateFormat(TIME_FORMAT).parse(value).getTime());
            
        } catch (ParseException e) {
            
            throw new DataSourceException(e);
        }
        
    }
    
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss.S"; 
    
    private Date parseDate(String value) throws ParseException {
        
        return new SimpleDateFormat(DATE_FORMAT).parse(value);
    }
    
    private Date parseDateTime(String value) throws ParseException {
        
        return new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT).parse(value);
    }
    
}
