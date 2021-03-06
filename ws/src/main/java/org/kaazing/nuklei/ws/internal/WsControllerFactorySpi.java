/**
 * Copyright 2007-2016, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.nuklei.ws.internal;

import org.kaazing.nuklei.Configuration;
import org.kaazing.nuklei.Controller;
import org.kaazing.nuklei.ControllerFactorySpi;

public final class WsControllerFactorySpi implements ControllerFactorySpi
{
    @Override
    public String name()
    {
        return "ws";
    }

    @Override
    public Class<WsController> kind()
    {
        return WsController.class;
    }

    @Override
    public <T extends Controller> T create(
        Class<T> kind,
        Configuration config)
    {
        Context context = new Context();
        context.readonly(true)
               .conclude(config);

        return kind.cast(new WsController(context));
    }

}
