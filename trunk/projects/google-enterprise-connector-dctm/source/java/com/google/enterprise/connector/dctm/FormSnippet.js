/**
 * Handles clicks on the Advanced Configuration checkbox.
 *
 * @param {HTLMInputElement} checkbox The checkbox object.
 * @param {boolean} isRendered Indicates if the Advanced Configuration
 *     form is present in the HTML form snippet.
 */
function clickAdvancedConfiguration(checkbox, isRendered) {
  if (document.getElementById('more').style.display == 'none') {
    if ((document.getElementById('login').value != '') &&
        (document.getElementById('Password').value != '')) {
      if (isRendered) {
        document.getElementById('more').style.display = '';
      } else {
        redisplayFormSnippet();
      }
    } else {
      alert(advanced_configuration_error);
      checkbox.checked = false;
    }
  } else {
    document.getElementById('more').style.display = 'none';
  }
}

/** Submits the form to be redisplayed. */
function redisplayFormSnippet() {
  document.getElementById('action_update').value = 'redisplay';
  document.body.style.cursor = 'wait';
  document.getElementsByTagName('input')[
      document.getElementsByTagName('input').length - 1].click();
}

/**
 * Moves options from one select list to another.
 *
 * @param {String} fromId The ID of the source select list.
 * @param {String} toId The ID of the destination select list.
 * @param {String} requiredError An error message for a required value;
 *     if this is defined, the source select list cannot be emptied.
 * @param {String} selectId The ID of the included options select list.
 * @param {String} hiddenId The ID of the hidden input containing the
 *     corresponding comma-separated string value.
 * @param {boolean} redisplay if defined, the form is submitted to be
 *     redisplayed.
 */
function swap(fromId, toId, requiredError, selectId, hiddenId, redisplay) {
  if (moveOptions(fromId, toId, requiredError)) {
    saveSelectToHidden(selectId, hiddenId);
    if (redisplay) {
      redisplayFormSnippet();
    }
  }
}

/**
 * Moves options from one select list to another.
 *
 * @param {String} fromId The ID of the source select list.
 * @param {String} toId The ID of the destination select list.
 * @param {String} requiredError An error message for a required value;
 *     if this is defined, the source select list cannot be emptied.
 * @return {boolean} true if the move was made, or false if the user
 *     cancelled it.
 */
// TODO: Two ways to improve this code, if it needs to run faster,
// would be to make sure the selected items are processed in order,
// and search the toList from the previous insertion point, and to
// use a binary search.
function moveOptions(fromId, toId, requiredError) {
  fromList = document.getElementsByName(fromId)[0];
  toList = document.getElementsByName(toId)[0];
  var count = 0;
  for (var i = 0; i < fromList.options.length; i++) {
    if (fromList.options[i].selected) {
      count++;
    }
  }
  if (count == fromList.options.length && requiredError) {
    alert(requiredError);
    return false;
  }
  while (fromList.selectedIndex != -1) {
    addOption(toList, fromList.options[fromList.selectedIndex]);
    fromList.options[fromList.selectedIndex] = null;
  }
  return true;
}

/**
 * Add an option to a select list in alphabetic order.
 *
 * @param {HTMLInputElement} list A select list.
 * @param {String} option An option value.
 */
function addOption(list, option) {
  var beforeOption = null;
  for (var i = 0; i < list.length; i++) {
    if (option.value < list.options[i].value) {
      beforeOption = list.options[i];
      break;
    }
  }
  var newOption = document.createElement('option');
  newOption.value = option.value;
  newOption.appendChild(document.createTextNode(option.value));
  if (beforeOption == null) {
    list.appendChild(newOption);
  } else {
    list.insertBefore(newOption, beforeOption);
  }
  // Workaround IE7 bug where the width shrinks on each insert.
  list.style.width = '';
  list.style.width = '100%';
}

/**
 * Saves the values of the selected options from the select list as
 * a comma-separate string in the hidden input element.
 *
 * @param {String} selectId The ID of the included options select list.
 * @param {String} hiddenId The ID of the hidden input containing the
 *     corresponding comma-separated string value.
 */
function saveSelectToHidden(selectId, hiddenId) {
  var txtInclude = document.getElementById(hiddenId);
  var selectedArray = new Array();
  var selObj = document.getElementById(selectId);
  for (var i = 0; i < selObj.options.length; i++) {
    selectedArray[i] = selObj.options[i].value;
  }
  txtInclude.value = selectedArray;
}
