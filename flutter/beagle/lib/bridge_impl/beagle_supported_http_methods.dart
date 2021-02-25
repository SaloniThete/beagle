/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import 'package:flutter_js/extensions/xhr.dart';

/// A map of Beagle supported HTTP methods.
class BeagleSupportedHttpMethods {
  final httpMethodMap = {
    'get': HttpMethod.get,
    'post': HttpMethod.post,
    'put': HttpMethod.put,
    'patch': HttpMethod.patch,
    'delete': HttpMethod.delete,
  };

  /// Returns the corresponding [HttpMethod].
  /// Throws [Exception] if the method passed by parameter isn't supported by
  /// Beagle.
  HttpMethod getHttpMethod(String httpMethod) {
    if (httpMethodMap.containsKey(httpMethod)) {
      return httpMethodMap[httpMethod];
    }

    throw Exception('Unsupported http method $httpMethod');
  }
}