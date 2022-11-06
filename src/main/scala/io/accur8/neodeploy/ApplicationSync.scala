package io.accur8.neodeploy

import a8.shared.json.JsonCodec
import io.accur8.neodeploy.resolvedmodel.ResolvedApp

abstract class ApplicationSync[A : JsonCodec] extends Sync[A, ResolvedApp]{

}
