
package better.recordstring

import groovy.transform.Canonical;

/**
 * Created by cz on 2017/7/24.
 */
@Canonical
class RecordConfiguration {
    List<String> filterString
    List<String> extrasString
    String buildStringFile
}
