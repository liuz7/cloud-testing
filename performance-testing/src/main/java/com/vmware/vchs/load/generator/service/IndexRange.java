/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.vmware.vchs.load.generator.service;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "cast", "rawtypes", "serial", "unchecked" })
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2015-1-18")
public class IndexRange implements org.apache.thrift.TBase<IndexRange, IndexRange._Fields>, java.io.Serializable, Cloneable, Comparable<IndexRange> {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("IndexRange");

    private static final org.apache.thrift.protocol.TField LOW_FIELD_DESC = new org.apache.thrift.protocol.TField("low", org.apache.thrift.protocol.TType.I32, (short) 1);
    private static final org.apache.thrift.protocol.TField HIGH_FIELD_DESC = new org.apache.thrift.protocol.TField("high", org.apache.thrift.protocol.TType.I32, (short) 2);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
        schemes.put(StandardScheme.class, new IndexRangeStandardSchemeFactory());
        schemes.put(TupleScheme.class, new IndexRangeTupleSchemeFactory());
    }

    public int low; // required
    public int high; // required

    /**
     * The set of fields this struct contains, along with convenience methods
     * for finding and manipulating them.
     */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
        LOW((short) 1, "low"), HIGH((short) 2, "high");

        private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

        static {
            for (_Fields field : EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, or null if its not
         * found.
         */
        public static _Fields findByThriftId(int fieldId) {
            switch (fieldId) {
            case 1: // LOW
                return LOW;
            case 2: // HIGH
                return HIGH;
            default:
                return null;
            }
        }

        /**
         * Find the _Fields constant that matches fieldId, throwing an exception
         * if it is not found.
         */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null)
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
         * Find the _Fields constant that matches name, or null if its not
         * found.
         */
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        private final short _thriftId;
        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    // isset id assignments
    private static final int __LOW_ISSET_ID = 0;
    private static final int __HIGH_ISSET_ID = 1;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
        Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.LOW, new org.apache.thrift.meta_data.FieldMetaData("low", org.apache.thrift.TFieldRequirementType.DEFAULT, new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
        tmpMap.put(_Fields.HIGH, new org.apache.thrift.meta_data.FieldMetaData("high", org.apache.thrift.TFieldRequirementType.DEFAULT, new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(IndexRange.class, metaDataMap);
    }

    public IndexRange() {
    }

    public IndexRange(int low, int high) {
        this();
        this.low = low;
        setLowIsSet(true);
        this.high = high;
        setHighIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public IndexRange(IndexRange other) {
        __isset_bitfield = other.__isset_bitfield;
        this.low = other.low;
        this.high = other.high;
    }

    public IndexRange deepCopy() {
        return new IndexRange(this);
    }

    @Override
    public void clear() {
        setLowIsSet(false);
        this.low = 0;
        setHighIsSet(false);
        this.high = 0;
    }

    public int getLow() {
        return this.low;
    }

    public IndexRange setLow(int low) {
        this.low = low;
        setLowIsSet(true);
        return this;
    }

    public void unsetLow() {
        __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __LOW_ISSET_ID);
    }

    /**
     * Returns true if field low is set (has been assigned a value) and false
     * otherwise
     */
    public boolean isSetLow() {
        return EncodingUtils.testBit(__isset_bitfield, __LOW_ISSET_ID);
    }

    public void setLowIsSet(boolean value) {
        __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __LOW_ISSET_ID, value);
    }

    public int getHigh() {
        return this.high;
    }

    public IndexRange setHigh(int high) {
        this.high = high;
        setHighIsSet(true);
        return this;
    }

    public void unsetHigh() {
        __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __HIGH_ISSET_ID);
    }

    /**
     * Returns true if field high is set (has been assigned a value) and false
     * otherwise
     */
    public boolean isSetHigh() {
        return EncodingUtils.testBit(__isset_bitfield, __HIGH_ISSET_ID);
    }

    public void setHighIsSet(boolean value) {
        __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __HIGH_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
        switch (field) {
        case LOW:
            if (value == null) {
                unsetLow();
            } else {
                setLow((Integer) value);
            }
            break;

        case HIGH:
            if (value == null) {
                unsetHigh();
            } else {
                setHigh((Integer) value);
            }
            break;

        }
    }

    public Object getFieldValue(_Fields field) {
        switch (field) {
        case LOW:
            return Integer.valueOf(getLow());

        case HIGH:
            return Integer.valueOf(getHigh());

        }
        throw new IllegalStateException();
    }

    /**
     * Returns true if field corresponding to fieldID is set (has been assigned
     * a value) and false otherwise
     */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }

        switch (field) {
        case LOW:
            return isSetLow();
        case HIGH:
            return isSetHigh();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof IndexRange)
            return this.equals((IndexRange) that);
        return false;
    }

    public boolean equals(IndexRange that) {
        if (that == null)
            return false;

        boolean this_present_low = true;
        boolean that_present_low = true;
        if (this_present_low || that_present_low) {
            if (!(this_present_low && that_present_low))
                return false;
            if (this.low != that.low)
                return false;
        }

        boolean this_present_high = true;
        boolean that_present_high = true;
        if (this_present_high || that_present_high) {
            if (!(this_present_high && that_present_high))
                return false;
            if (this.high != that.high)
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        List<Object> list = new ArrayList<Object>();

        boolean present_low = true;
        list.add(present_low);
        if (present_low)
            list.add(low);

        boolean present_high = true;
        list.add(present_high);
        if (present_high)
            list.add(high);

        return list.hashCode();
    }

    @Override
    public int compareTo(IndexRange other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }

        int lastComparison = 0;

        lastComparison = Boolean.valueOf(isSetLow()).compareTo(other.isSetLow());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetLow()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.low, other.low);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetHigh()).compareTo(other.isSetHigh());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetHigh()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.high, other.high);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
        schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
        schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("IndexRange(");
        boolean first = true;

        sb.append("low:");
        sb.append(this.low);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("high:");
        sb.append(this.high);
        first = false;
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
        // check for required fields
        // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        try {
            write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        try {
            // it doesn't seem like you should have to do this, but java
            // serialization is wacky, and doesn't call the default constructor.
            __isset_bitfield = 0;
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private static class IndexRangeStandardSchemeFactory implements SchemeFactory {
        public IndexRangeStandardScheme getScheme() {
            return new IndexRangeStandardScheme();
        }
    }

    private static class IndexRangeStandardScheme extends StandardScheme<IndexRange> {

        public void read(org.apache.thrift.protocol.TProtocol iprot, IndexRange struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch (schemeField.id) {
                case 1: // LOW
                    if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                        struct.low = iprot.readI32();
                        struct.setLowIsSet(true);
                    } else {
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                    }
                    break;
                case 2: // HIGH
                    if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                        struct.high = iprot.readI32();
                        struct.setHighIsSet(true);
                    } else {
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                    }
                    break;
                default:
                    org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();

            // check for required fields of primitive type, which can't be
            // checked in the validate method
            struct.validate();
        }

        public void write(org.apache.thrift.protocol.TProtocol oprot, IndexRange struct) throws org.apache.thrift.TException {
            struct.validate();

            oprot.writeStructBegin(STRUCT_DESC);
            oprot.writeFieldBegin(LOW_FIELD_DESC);
            oprot.writeI32(struct.low);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(HIGH_FIELD_DESC);
            oprot.writeI32(struct.high);
            oprot.writeFieldEnd();
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }

    }

    private static class IndexRangeTupleSchemeFactory implements SchemeFactory {
        public IndexRangeTupleScheme getScheme() {
            return new IndexRangeTupleScheme();
        }
    }

    private static class IndexRangeTupleScheme extends TupleScheme<IndexRange> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, IndexRange struct) throws org.apache.thrift.TException {
            TTupleProtocol oprot = (TTupleProtocol) prot;
            BitSet optionals = new BitSet();
            if (struct.isSetLow()) {
                optionals.set(0);
            }
            if (struct.isSetHigh()) {
                optionals.set(1);
            }
            oprot.writeBitSet(optionals, 2);
            if (struct.isSetLow()) {
                oprot.writeI32(struct.low);
            }
            if (struct.isSetHigh()) {
                oprot.writeI32(struct.high);
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, IndexRange struct) throws org.apache.thrift.TException {
            TTupleProtocol iprot = (TTupleProtocol) prot;
            BitSet incoming = iprot.readBitSet(2);
            if (incoming.get(0)) {
                struct.low = iprot.readI32();
                struct.setLowIsSet(true);
            }
            if (incoming.get(1)) {
                struct.high = iprot.readI32();
                struct.setHighIsSet(true);
            }
        }
    }

}
