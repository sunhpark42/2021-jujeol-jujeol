export {getRequest, postRequest, deleteRequest, putRequest};

const BEARER = 'Bearer'
const CAN_METHOD_LIST = ['GET', 'POST', 'PUT', 'DELETE']

const orThrow = (result) => {
  if (result.code) {
    console.error(result)
    new Error(result)
  }
  return result;
}

function getRequest(url, needToken = false) {
  return apiRequest('GET', url, needToken);
}

function postRequest(url, body = {}, needToken = false) {
  return apiRequest('POST', url, needToken, body);
}

function deleteRequest(url, needToken = false) {
  return apiRequest('DELETE', url, needToken)
}

function putRequest(url, body = {}, needToken = false) {
  return apiRequest('PUT', url, needToken, body)
}

async function apiRequest(method, url = '', needToken = false, body = {}) {
  const isMethod = CAN_METHOD_LIST.findIndex(canMethod => canMethod === method.toUpperCase()) !== -1
  if (!method || !isMethod) {
    throw new Error(`not valid method : ${method} / can method : get, post, delete, put`);
  }

  const headers = {'Content-Type': 'application/json'};
  if (needToken) {
    headers.Authorization = `${BEARER} ${localStorage.getItem('accessToken')}`
  }

  const configure = {
    method: method,
    headers: headers,
    credentials: 'include',
  }

  if (body && method.toUpperCase() !== 'GET') {
    configure.body = JSON.stringify(body)
  }

  let result = await fetch(url, configure)
  .then(response =>
      response.text()
  ).then(text => {
        if (text) {
          return JSON.parse(text)
        } else
          return {};
      }
  )

  return orThrow(result)
}