package net.sf.anathema.hero.sheet.pdf.encoder.table;

import com.itextpdf.text.DocumentException;
import net.sf.anathema.hero.sheet.pdf.encoder.general.Bounds;
import net.sf.anathema.hero.sheet.pdf.encoder.graphics.SheetGraphics;

public interface ITableEncoder<C> {

  float encodeTable(SheetGraphics graphics, C content, Bounds bounds) throws DocumentException;

  boolean hasContent(C content);
}
