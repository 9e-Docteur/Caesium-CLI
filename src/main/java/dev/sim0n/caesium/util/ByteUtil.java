package dev.sim0n.caesium.util;

import dev.sim0n.caesium.util.classwriter.CaesiumClassWriter;
import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

@UtilityClass
public class ByteUtil {

    /**
     * Converts the provided byte array into a {@link ClassNode}.
     * @param bytes The byte array to convert into a {@link ClassNode}
     * @return A class node from the provided byte array
     */
    public ClassNode parseClassBytes(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();

        reader.accept(classNode, 0);

        return classNode;
    }

    /**
     * Converts the provided {@link ClassNode} to a byte array.
     * @param classNode The class node to convert to a byte array
     * @return A byte array from the provided {@link ClassNode}
     */
    public byte[] getClassBytes(ClassNode classNode) {
        CaesiumClassWriter classWriter = new CaesiumClassWriter(CaesiumClassWriter.COMPUTE_FRAMES);

        classWriter.newUTF8("caesium");
        classNode.accept(classWriter);

        return classWriter.toByteArray();
    }

    /**
     * Converts bytes to kilobytes.
     * @param bytes The bytes to convert
     * @return The provided bytes in kilobytes
     */
    public double bytesToKB(long bytes) {
        return bytes / 1024D;
    }
}
