package xyz.morphia.ext;


import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import xyz.morphia.TestBase;
import xyz.morphia.annotations.Converters;
import xyz.morphia.annotations.Id;
import xyz.morphia.converters.SimpleValueConverter;
import xyz.morphia.converters.TypeConverter;
import xyz.morphia.mapping.MappedField;


/**
 * Example converter which stores the enum value instead of string (name)
 *
 * @author scotthernandez
 */
public class EnumValueConverterTest extends TestBase {

    @Test
    public void testEnum() {
        final EnumEntity ee = new EnumEntity();
        getDs().save(ee);
        final DBObject dbObj = getDs().getCollection(EnumEntity.class).findOne();
        Assert.assertEquals(1, dbObj.get("val"));
    }

    private enum AEnum {
        One,
        Two

    }

    private static class AEnumConverter extends TypeConverter implements SimpleValueConverter {

        AEnumConverter() {
            super(AEnum.class);
        }

        @Override
        public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
            if (fromDBObject == null) {
                return null;
            }
            return AEnum.values()[(Integer) fromDBObject];
        }

        @Override
        public Object encode(final Object value, final MappedField optionalExtraInfo) {
            if (value == null) {
                return null;
            }

            return ((Enum) value).ordinal();
        }
    }

    @Converters(AEnumConverter.class)
    private static class EnumEntity {
        @Id
        private ObjectId id = new ObjectId();
        private AEnum val = AEnum.Two;

    }
}
