package io.accur8.neodeploy

import a8.shared.json.JsonCodec
import io.accur8.neodeploy.model.ResolvedApp

abstract class ApplicationSync[A : JsonCodec] extends Sync[A, ResolvedApp]{

}
