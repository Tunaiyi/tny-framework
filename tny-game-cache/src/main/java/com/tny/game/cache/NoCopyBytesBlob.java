package com.tny.game.cache;

import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.sql.*;
import java.util.Arrays;

/**
 * Created by Kun Yang on 2018/2/27.
 */
public class NoCopyBytesBlob implements Blob, Serializable {

    /**
     * A serialized array of uninterpreted bytes representing the
     * value of this <code>SerialBlob</code> object.
     *
     * @serial
     */
    private byte[] buf;

    /**
     * The number of bytes in this <code>SerialBlob</code> object's
     * array of bytes.
     *
     * @serial
     */
    private long len;

    /**
     * The original number of bytes in this <code>SerialBlob</code> object's
     * array of bytes when it was first established.
     *
     * @serial
     */
    private long origLen;

    /**
     * Constructs a <code>SerialBlob</code> object that is a serialized version of
     * the given <code>byte</code> array.
     * <p>
     * The new <code>SerialBlob</code> object is initialized with the data from the
     * <code>byte</code> array, thus allowing disconnected <code>RowSet</code>
     * objects to establish serialized <code>Blob</code> objects without
     * touching the data source.
     *
     * @param b the <code>byte</code> array containing the data for the
     *          <code>Blob</code> object to be serialized
     * @throws SerialException if an error occurs during serialization
     * @throws SQLException    if a SQL errors occurs
     */
    public NoCopyBytesBlob(byte[] b)
            throws SerialException, SQLException {
        len = b.length;
        buf = b;
        origLen = len;
    }


    /**
     * Copies the specified number of bytes, starting at the given
     * position, from this <code>SerialBlob</code> object to
     * another array of bytes.
     * <p>
     * Note that if the given number of bytes to be copied is larger than
     * the length of this <code>SerialBlob</code> object's array of
     * bytes, the given number will be shortened to the array's length.
     *
     * @param pos    the ordinal position of the first byte in this
     *               <code>SerialBlob</code> object to be copied;
     *               numbering starts at <code>1</code>; must not be less
     *               than <code>1</code> and must be less than or equal
     *               to the length of this <code>SerialBlob</code> object
     * @param length the number of bytes to be copied
     * @return an array of bytes that is a copy of a region of this
     * <code>SerialBlob</code> object, starting at the given
     * position and containing the given number of consecutive bytes
     * @throws SerialException if the given starting position is out of bounds;
     *                         if {@code free} had previously been called on this object
     */
    @Override
    public byte[] getBytes(long pos, int length) throws SerialException {
        isValid();
        if (length > len) {
            length = (int) len;
        }

        if (pos < 1 || len - pos < 0) {
            throw new SerialException("Invalid arguments: position cannot be "
                                      + "less than 1 or greater than the length of the SerialBlob");
        }

        pos--; // correct pos to array index

        byte[] b = new byte[length];

        for (int i = 0; i < length; i++) {
            b[i] = this.buf[(int) pos];
            pos++;
        }
        return b;
    }

    /**
     * Retrieves the number of bytes in this <code>SerialBlob</code>
     * object's array of bytes.
     *
     * @return a <code>long</code> indicating the length in bytes of this
     * <code>SerialBlob</code> object's array of bytes
     * @throws SerialException if an error occurs;
     *                         if {@code free} had previously been called on this object
     */
    @Override
    public long length() throws SerialException {
        isValid();
        return len;
    }

    /**
     * Returns this <code>SerialBlob</code> object as an input stream.
     * Unlike the related method, <code>setBinaryStream</code>,
     * a stream is produced regardless of whether the <code>SerialBlob</code>
     * was created with a <code>Blob</code> object or a <code>byte</code> array.
     *
     * @return a <code>java.io.InputStream</code> object that contains
     * this <code>SerialBlob</code> object's array of bytes
     * @throws SerialException if an error occurs;
     *                         if {@code free} had previously been called on this object
     * @see #setBinaryStream
     */
    @Override
    public java.io.InputStream getBinaryStream() throws SerialException {
        isValid();
        InputStream stream = new ByteArrayInputStream(buf);
        return stream;
    }

    /**
     * Returns the position in this <code>SerialBlob</code> object where
     * the given pattern of bytes begins, starting the search at the
     * specified position.
     *
     * @param pattern the pattern of bytes for which to search
     * @param start   the position of the byte in this
     *                <code>SerialBlob</code> object from which to begin
     *                the search; the first position is <code>1</code>;
     *                must not be less than <code>1</code> nor greater than
     *                the length of this <code>SerialBlob</code> object
     * @return the position in this <code>SerialBlob</code> object
     * where the given pattern begins, starting at the specified
     * position; <code>-1</code> if the pattern is not found
     * or the given starting position is out of bounds; position
     * numbering for the return value starts at <code>1</code>
     * @throws SerialException if an error occurs when serializing the blob;
     *                         if {@code free} had previously been called on this object
     * @throws SQLException    if there is an error accessing the <code>BLOB</code>
     *                         value from the database
     */
    @Override
    public long position(byte[] pattern, long start)
            throws SerialException, SQLException {

        isValid();
        if (start < 1 || start > len) {
            return -1;
        }

        int pos = (int) start - 1; // internally Blobs are stored as arrays.
        int i = 0;
        long patlen = pattern.length;

        while (pos < len) {
            if (pattern[i] == buf[pos]) {
                if (i + 1 == patlen) {
                    return (pos + 1) - (patlen - 1);
                }
                i++;
                pos++; // increment pos, and i
            } else if (pattern[i] != buf[pos]) {
                pos++; // increment pos only
            }
        }
        return -1; // not found
    }

    /**
     * Returns the position in this <code>SerialBlob</code> object where
     * the given <code>Blob</code> object begins, starting the search at the
     * specified position.
     *
     * @param pattern the <code>Blob</code> object for which to search;
     * @param start   the position of the byte in this
     *                <code>SerialBlob</code> object from which to begin
     *                the search; the first position is <code>1</code>;
     *                must not be less than <code>1</code> nor greater than
     *                the length of this <code>SerialBlob</code> object
     * @return the position in this <code>SerialBlob</code> object
     * where the given <code>Blob</code> object begins, starting
     * at the specified position; <code>-1</code> if the pattern is
     * not found or the given starting position is out of bounds;
     * position numbering for the return value starts at <code>1</code>
     * @throws SerialException if an error occurs when serializing the blob;
     *                         if {@code free} had previously been called on this object
     * @throws SQLException    if there is an error accessing the <code>BLOB</code>
     *                         value from the database
     */
    @Override
    public long position(Blob pattern, long start)
            throws SerialException, SQLException {
        isValid();
        return position(pattern.getBytes(1, (int) (pattern.length())), start);
    }

    /**
     * Writes the given array of bytes to the <code>BLOB</code> value that
     * this <code>Blob</code> object represents, starting at position
     * <code>pos</code>, and returns the number of bytes written.
     *
     * @param pos   the position in the SQL <code>BLOB</code> value at which
     *              to start writing. The first position is <code>1</code>;
     *              must not be less than <code>1</code> nor greater than
     *              the length of this <code>SerialBlob</code> object.
     * @param bytes the array of bytes to be written to the <code>BLOB</code>
     *              value that this <code>Blob</code> object represents
     * @return the number of bytes written
     * @throws SerialException if there is an error accessing the
     *                         <code>BLOB</code> value; or if an invalid position is set; if an
     *                         invalid offset value is set;
     *                         if {@code free} had previously been called on this object
     * @throws SQLException    if there is an error accessing the <code>BLOB</code>
     *                         value from the database
     * @see #getBytes
     */
    @Override
    public int setBytes(long pos, byte[] bytes)
            throws SerialException, SQLException {
        return setBytes(pos, bytes, 0, bytes.length);
    }

    /**
     * Writes all or part of the given <code>byte</code> array to the
     * <code>BLOB</code> value that this <code>Blob</code> object represents
     * and returns the number of bytes written.
     * Writing starts at position <code>pos</code> in the <code>BLOB</code>
     * value; <i>len</i> bytes from the given byte array are written.
     *
     * @param pos    the position in the <code>BLOB</code> object at which
     *               to start writing. The first position is <code>1</code>;
     *               must not be less than <code>1</code> nor greater than
     *               the length of this <code>SerialBlob</code> object.
     * @param bytes  the array of bytes to be written to the <code>BLOB</code>
     *               value
     * @param offset the offset in the <code>byte</code> array at which
     *               to start reading the bytes. The first offset position is
     *               <code>0</code>; must not be less than <code>0</code> nor greater
     *               than the length of the <code>byte</code> array
     * @param length the number of bytes to be written to the
     *               <code>BLOB</code> value from the array of bytes <i>bytes</i>.
     * @return the number of bytes written
     * @throws SerialException if there is an error accessing the
     *                         <code>BLOB</code> value; if an invalid position is set; if an
     *                         invalid offset value is set; if number of bytes to be written
     *                         is greater than the <code>SerialBlob</code> length; or the combined
     *                         values of the length and offset is greater than the Blob buffer;
     *                         if {@code free} had previously been called on this object
     * @throws SQLException    if there is an error accessing the <code>BLOB</code>
     *                         value from the database.
     * @see #getBytes
     */
    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int length)
            throws SerialException, SQLException {

        isValid();
        if (offset < 0 || offset > bytes.length) {
            throw new SerialException("Invalid offset in byte array set");
        }

        if (pos < 1 || pos > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }

        if ((long) (length) > origLen) {
            throw new SerialException("Buffer is not sufficient to hold the value");
        }

        if ((length + offset) > bytes.length) {
            throw new SerialException("Invalid OffSet. Cannot have combined offset " +
                                      "and length that is greater that the Blob buffer");
        }

        int i = 0;
        pos--; // correct to array indexing
        while (i < length || (offset + i + 1) < (bytes.length - offset)) {
            this.buf[(int) pos + i] = bytes[offset + i];
            i++;
        }
        return i;
    }

    /**
     * Retrieves a stream that can be used to write to the <code>BLOB</code>
     * value that this <code>Blob</code> object represents.  The stream begins
     * at position <code>pos</code>. This method forwards the
     * <code>setBinaryStream()</code> call to the underlying <code>Blob</code> in
     * the event that this <code>SerialBlob</code> object is instantiated with a
     * <code>Blob</code>. If this <code>SerialBlob</code> is instantiated with
     * a <code>byte</code> array, a <code>SerialException</code> is thrown.
     *
     * @param pos the position in the <code>BLOB</code> value at which
     *            to start writing
     * @return a <code>java.io.OutputStream</code> object to which data can
     * be written
     * @throws SQLException    if there is an error accessing the
     *                         <code>BLOB</code> value
     * @throws SerialException if the SerialBlob in not instantiated with a
     *                         <code>Blob</code> object that supports <code>setBinaryStream()</code>;
     *                         if {@code free} had previously been called on this object
     * @see #getBinaryStream
     */
    @Override
    public java.io.OutputStream setBinaryStream(long pos) throws SQLException {
        throw new SerialException("Unsupported operation. SerialBlob cannot " +
                                  "return a writable binary stream, unless instantiated with a Blob object " +
                                  "that provides a setBinaryStream() implementation");
    }

    /**
     * Truncates the <code>BLOB</code> value that this <code>Blob</code>
     * object represents to be <code>len</code> bytes in length.
     *
     * @param length the length, in bytes, to which the <code>BLOB</code>
     *               value that this <code>Blob</code> object represents should be
     *               truncated
     * @throws SerialException if there is an error accessing the Blob value;
     *                         or the length to truncate is greater that the SerialBlob length;
     *                         if {@code free} had previously been called on this object
     */
    @Override
    public void truncate(long length) throws SerialException {
        isValid();
        if (length > len) {
            throw new SerialException(
                    "Length more than what can be truncated");
        } else if ((int) length == 0) {
            buf = new byte[0];
            len = length;
        } else {
            len = length;
            buf = this.getBytes(1, (int) len);
        }
    }


    /**
     * Returns an
     * <code>InputStream</code> object that contains a partial
     * {@code Blob} value, starting with the byte specified by pos, which is
     * length bytes in length.
     *
     * @param pos    the offset to the first byte of the partial value to be
     *               retrieved. The first byte in the {@code Blob} is at position 1
     * @param length the length in bytes of the partial value to be retrieved
     * @return <code>InputStream</code> through which the partial {@code Blob} value can
     * be read.
     * @throws SQLException    if pos is less than 1 or if pos is greater than the
     *                         number of bytes in the {@code Blob} or if pos + length is greater than
     *                         the number of bytes in the {@code Blob}
     * @throws SerialException if the {@code free} method had been previously
     *                         called on this object
     * @since 1.6
     */
    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        isValid();
        if (pos < 1 || pos > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }
        if (length < 1 || length > len - pos + 1) {
            throw new SerialException(
                    "length is < 1 or pos + length > total number of bytes");
        }
        return new ByteArrayInputStream(buf, (int) pos - 1, (int) length);
    }


    /**
     * This method frees the {@code SeriableBlob} object and releases the
     * resources that it holds. The object is invalid once the {@code free}
     * method is called. <p> If {@code free} is called multiple times, the
     * subsequent calls to {@code free} are treated as a no-op. </P>
     *
     * @throws SQLException if an error occurs releasing the Blob's resources
     * @since 1.6
     */
    @Override
    public void free() throws SQLException {
        if (buf != null) {
            buf = null;
        }
    }

    /**
     * Compares this SerialBlob to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null} and is a {@code
     * SerialBlob} object that represents the same sequence of bytes as this
     * object.
     *
     * @param obj The object to compare this {@code SerialBlob} against
     * @return {@code true} if the given object represents a {@code SerialBlob}
     * equivalent to this SerialBlob, {@code false} otherwise
     */
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NoCopyBytesBlob) {
            NoCopyBytesBlob sb = (NoCopyBytesBlob) obj;
            if (this.len == sb.len) {
                return Arrays.equals(buf, sb.buf);
            }
        }
        return false;
    }

    /**
     * Returns a hash code for this {@code SerialBlob}.
     *
     * @return a hash code value for this object.
     */
    public int hashCode() {
        return ((31 + Arrays.hashCode(buf)) * 31 + (int) len) * 31 + (int) origLen;
    }

    /**
     * readObject is called to restore the state of the SerialBlob from
     * a stream.
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = s.readFields();
        byte[] tmp = (byte[]) fields.get("buf", null);
        if (tmp == null)
            throw new InvalidObjectException("buf is null and should not be!");
        buf = tmp.clone();
        len = fields.get("len", 0L);
        if (buf.length != len)
            throw new InvalidObjectException("buf is not the expected size");
        origLen = fields.get("origLen", 0L);
    }

    /**
     * writeObject is called to save the state of the SerialBlob
     * to a stream.
     */
    private void writeObject(ObjectOutputStream s)
            throws IOException, ClassNotFoundException {

        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("buf", buf);
        fields.put("len", len);
        fields.put("origLen", origLen);
        s.writeFields();
    }

    /**
     * Check to see if this object had previously had its {@code free} method
     * called
     *
     * @throws SerialException
     */
    private void isValid() throws SerialException {
        if (buf == null) {
            throw new SerialException("Error: You cannot call a method on a " +
                                      "SerialBlob instance once free() has been called.");
        }
    }

    /**
     * The identifier that assists in the serialization of this
     * {@code SerialBlob} object.
     */
    static final long serialVersionUID = -8144641928112860441L;
}
