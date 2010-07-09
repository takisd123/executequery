package org.executequery.actions.queryeditor;

import java.awt.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.executequery.log.Log;
import org.executequery.repository.KeywordRepository;
import org.executequery.repository.RepositoryCache;

/** 
 *
 * @author   Takis Diakoumis
 * @version  $Revision: 1460 $
 * @date     $Date: 2009-01-25 11:06:46 +1100 (Sun, 25 Jan 2009) $
 */
public class AddToUserDefinedKeywordsCommand extends AbstractQueryEditorCommand {

    public void execute(ActionEvent e) {

        if (isQueryEditorTheCentralPanel()) {

            if (queryEditor().hasText()) {

                String wordAtCursor = queryEditor().getCompleteWordEndingAtCursor();
                if (StringUtils.isNotBlank(wordAtCursor)) {

                    wordAtCursor = wordAtCursor.toUpperCase();

                    KeywordRepository keywordRepository =
                        (KeywordRepository)RepositoryCache.load(KeywordRepository.REPOSITORY_ID);

                    if (!keywordRepository.contains(wordAtCursor)) {

                        keywordRepository.addUserDefinedKeyword(wordAtCursor);
                        Log.info("Keyword [ " + wordAtCursor + 
                                " ] added to user defined keyword list.");
                        
                    } else {
                        
                        Log.info("Keyword already exists as a part of the defined keyword list.");
                    }
                    
                }
                
            }
            
        }

    }

}
